package com.receipt.receiptPhase.dto.report;

public class UpdatedChequeReport {

    private String transactionNo;
    private String originalChequeNo;
    private String newChequeNo;
    private String userId;
    private String actionDate;
    private String reason;

    public UpdatedChequeReport() {}

    public UpdatedChequeReport(String transactionNo, String originalChequeNo, String newChequeNo, String userId, String actionDate, String reason) {
        this.transactionNo = transactionNo;
        this.originalChequeNo = originalChequeNo;
        this.newChequeNo = newChequeNo;
        this.userId = userId;
        this.actionDate = actionDate;
        this.reason = reason;
    }

    // Getters and Setters
    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }

    public String getOriginalChequeNo() { return originalChequeNo; }
    public void setOriginalChequeNo(String originalChequeNo) { this.originalChequeNo = originalChequeNo; }

    public String getNewChequeNo() { return newChequeNo; }
    public void setNewChequeNo(String newChequeNo) { this.newChequeNo = newChequeNo; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActionDate() { return actionDate; }
    public void setActionDate(String actionDate) { this.actionDate = actionDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}