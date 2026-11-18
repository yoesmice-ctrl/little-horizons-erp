package com.littlehorizons.erp.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

/**
 * Utility class to generate simulated DIAN CUFE codes using SHA-384 hashing.
 */
public final class CufeGenerator {

    private static final String SCHOOL_NIT = "900845123-7";
    private static final String TECHNICAL_KEY = "fc8eac422eba16e22ffd8c6f94b3f40a6e38162c";
    private static final String ENVIRONMENT_TEST = "2";

    private CufeGenerator() {
    }

    public static String generateCufe(String invoiceNumber, LocalDate issueDate, BigDecimal totalAmount, String guardianDocument) {
        String rawData = String.format("%s|%s|%.2f|0.00|%.2f|%s|%s|%s|%s",
                invoiceNumber,
                issueDate.toString(),
                totalAmount,
                totalAmount,
                SCHOOL_NIT,
                guardianDocument,
                TECHNICAL_KEY,
                ENVIRONMENT_TEST
        );

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            byte[] encodedHash = digest.digest(rawData.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            ErrorLogger.logError("CufeGenerator", "SHA-384 algorithm not available", e);
            return "CUFE-FALLBACK-" + invoiceNumber + "-" + System.currentTimeMillis();
        }
    }
}
