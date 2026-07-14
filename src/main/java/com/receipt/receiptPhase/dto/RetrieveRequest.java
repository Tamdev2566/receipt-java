package com.receipt.receiptPhase.dto;

public class RetrieveRequest {
    private String invoiceNo;
    private String chequeNo;
    private String blNo;

    // Getters and Setters
    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public String getChequeNo() { return chequeNo; }
    public void setChequeNo(String chequeNo) { this.chequeNo = chequeNo; }
    public String getBlNo() { return blNo; }
    public void setBlNo(String blNo) { this.blNo = blNo; }
}

