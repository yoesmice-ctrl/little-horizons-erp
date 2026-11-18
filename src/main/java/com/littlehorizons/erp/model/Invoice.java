package com.littlehorizons.erp.model;

import java.time.LocalDate;

public class Invoice {
    private int invoiceId;
    private String invoiceNumber;
    private int paymentId;
    private LocalDate issueDate;
    private String cufe;

    public Invoice() {}

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public String getCufe() { return cufe; }
    public void setCufe(String cufe) { this.cufe = cufe; }
}
