package com.littlehorizons.erp.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class responsible for logging errors and system events to error_log.txt.
 */
public final class ErrorLogger {

    private static final String LOG_FILE_PATH = "error_log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ErrorLogger() {
    }

    public static synchronized void logError(String context, String message, Throwable throwable) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.printf("[%s] [ERROR] [%s] %s%n", timestamp, context, message);
            if (throwable != null) {
                out.printf("Exception: %s: %s%n", throwable.getClass().getName(), throwable.getMessage());
                for (StackTraceElement element : throwable.getStackTrace()) {
                    out.printf("    at %s%n", element.toString());
                }
            }
            out.println("--------------------------------------------------------------------------------");
        } catch (IOException ioException) {
            System.err.println("Critical error writing to error_log.txt: " + ioException.getMessage());
        }
    }
}
