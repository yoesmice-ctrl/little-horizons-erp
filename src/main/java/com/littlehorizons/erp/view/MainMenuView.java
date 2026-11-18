package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.util.InputValidator;

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
        return InputValidator.readIntInRange("\nSelect an option (1-7): ", 1, 7);
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
