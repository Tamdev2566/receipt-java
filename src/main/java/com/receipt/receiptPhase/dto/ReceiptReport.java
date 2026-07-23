package com.receipt.receiptPhase.dto;

public class ReceiptReport{
    private String transactionDate;
    private String paymentMode;
    private String currencyCode;
    private String reportFor; // Matches createdUser

    // Getters and Setters
    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getReportFor() { return reportFor; }
    public void setReportFor(String reportFor) { this.reportFor = reportFor; }
}
