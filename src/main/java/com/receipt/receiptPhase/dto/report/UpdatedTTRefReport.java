package com.receipt.receiptPhase.dto.report;

public class UpdatedTTRefReport {

    private String transactionNo;
    private String originalTtNo;
    private String newTtNo;
    private String userId;
    private String actionDate;
    private String reason;

    public UpdatedTTRefReport() {}

    public UpdatedTTRefReport(String transactionNo, String originalTtNo, String newTtNo, String userId, String actionDate, String reason) {
        this.transactionNo = transactionNo;
        this.originalTtNo = originalTtNo;
        this.newTtNo = newTtNo;
        this.userId = userId;
        this.actionDate = actionDate;
        this.reason = reason;
    }

    // Getters and Setters
    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }

    public String getOriginalTtNo() { return originalTtNo; }
    public void setOriginalTtNo(String originalTtNo) { this.originalTtNo = originalTtNo; }

    public String getNewTtNo() { return newTtNo; }
    public void setNewTtNo(String newTtNo) { this.newTtNo = newTtNo; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActionDate() { return actionDate; }
    public void setActionDate(String actionDate) { this.actionDate = actionDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}