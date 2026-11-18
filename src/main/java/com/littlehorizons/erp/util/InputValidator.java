package com.littlehorizons.erp.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utility class for safe and validated console user input.
 */
public final class InputValidator {

    private static final Scanner SCANNER = new Scanner(System.in);

    private InputValidator() {
    }

    public static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[!] Input cannot be empty. Please try again.");
        }
    }

    public static String readOptionalString(String prompt) {
        System.out.print(prompt);
        String input = SCANNER.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("[!] Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid integer format. Please try again.");
            }
        }
    }

    public static BigDecimal readBigDecimalInRange(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim().replace(",", ".");
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.doubleValue() >= min && value.doubleValue() <= max) {
                    return value;
                }
                System.out.printf("[!] Value must be between %.1f and %.1f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid decimal number. Please try again.");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String input = SCANNER.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("[!] Invalid date format. Use YYYY-MM-DD (e.g., 2026-11-17).");
            }
        }
    }

    public static void pressEnterToContinue() {
        System.out.print("\nPress ENTER to continue...");
        SCANNER.nextLine();
    }
}
