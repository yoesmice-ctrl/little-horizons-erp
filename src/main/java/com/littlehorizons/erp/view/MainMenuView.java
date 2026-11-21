package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.util.InputValidator;

import java.util.Scanner;

public class MainMenuView {

    public int showMainMenu() {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║   ERP Colegio Little Horizons                ║");
        System.out.println("║   Educational Management System              ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("1. Student Management & Enrollment");
        System.out.println("2. Academic Management (Grades & Attendance)");
        System.out.println("3. Extracurricular Activities (Clubs & Events)");
        System.out.println("4. Financial Management & DIAN Invoicing");
        System.out.println("5. Resource Management (Library & Labs)");
        System.out.println("6. Executive Reports & Statistics (Streams)");
        System.out.println("7. Exit");
        System.out.println("8. TEST - Importar Asistencias desde CSV");
        return InputValidator.readIntInRange("\nSelect an option (1-8): ", 1, 8);
    }

    public void showAttendanceImportFileNotFound() {
        System.out.println("Error: El archivo 'asistencias_importar.csv' no se encuentra en la carpeta raíz del proyecto.");
    }

    public void showAttendanceImportInvalidHeader() {
        System.out.println("Error: El archivo no tiene el formato correcto. La cabecera debe ser: id_estudiante;id_materia;fecha;estado");
    }

    public void showAttendanceImportReadError() {
        System.out.println("Error: No se pudo leer el archivo 'asistencias_importar.csv'. Revise el archivo error_log.txt.");
    }

    public void showAttendanceImportReport(int total, int success, int failed) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║           REPORTE DE IMPORTACIÓN DE ASISTENCIAS                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Total de líneas en el archivo: " + total);
        System.out.println("Líneas procesadas exitosamente: " + success);
        System.out.println("Líneas con errores (no procesadas): " + failed);
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════════");
        System.out.println("Presione Enter para volver al menú principal...");
        System.out.println("═══════════════════════════════════════════════════════════════════");
        new Scanner(System.in).nextLine();
    }

    public void printDianElectronicInvoice(Invoice invoice, Payment payment, String guardianDocument) {
        System.out.println("\n======================================================================");
        System.out.println("                DIAN ELECTRONIC INVOICE (SIMULATED)                   ");
        System.out.println("======================================================================");
        System.out.println("Issuer      : Colegio Internacional Little Horizons S.A.S.");
        System.out.println("NIT         : 900.845.123-7");
        System.out.println("Address     : Av. Principal #10-45, Cucuta, Colombia");
        System.out.println("DIAN Auth   : Resolution No. 18764000001234 (FE-000001 to FE-999999)");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("Invoice No  : %-20s | Issue Date: %s%n", invoice.getInvoiceNumber(), invoice.getIssueDate());
        System.out.printf("Receipt No  : %-20s | Pay Method: %s%n", payment.getReceiptNumber(), payment.getPaymentMethod());
        System.out.printf("Student ID  : %-20d | Payer Doc : %s%n", payment.getStudentId(), guardianDocument);
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("Concept ID  : %-20d | Amount    : $%,.2f COP%n", payment.getConceptId(), payment.getAmountPaid());
        System.out.println("VAT (IVA)   : $0.00 COP (Educational service exempt)");
        System.out.printf("TOTAL PAID  : $%,.2f COP%n", payment.getAmountPaid());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("CUFE (SHA-384):");
        System.out.println(invoice.getCufe());
        System.out.println("QR Validation URL: https://dian.gov.co" + invoice.getCufe().substring(0, 24));
        System.out.println("======================================================================\n");
    }
}
