package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.FinanceDAO;
import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.util.CufeGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class FinanceService {

    private final FinanceDAO financeDAO;

    public FinanceService() {
        this.financeDAO = new FinanceDAO();
    }

    public Invoice processPaymentAndGenerateInvoice(Payment payment, String guardianDocument) {
        String invoiceNumber = "FE-" + String.format("%06d", (int) (Math.random() * 900000 + 100000));
        if (payment.getReceiptNumber() == null || payment.getReceiptNumber().isBlank()) {
            payment.setReceiptNumber("REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        LocalDate today = LocalDate.now();
        payment.setPaymentDate(today);

        String cufe = CufeGenerator.generateCufe(invoiceNumber, today, payment.getAmountPaid(), guardianDocument);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setIssueDate(today);
        invoice.setCufe(cufe);

        boolean saved = financeDAO.registerPaymentAndInvoice(payment, invoice);
        return saved ? invoice : null;
    }

    /**
     * Calculates total revenue collected using Java Stream API.
     */
    public BigDecimal calculateTotalRevenue() {
        return financeDAO.getAllPayments().stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Payment> getStudentPaymentHistory(int studentId) {
        return financeDAO.getPaymentsByStudent(studentId);
    }
}
