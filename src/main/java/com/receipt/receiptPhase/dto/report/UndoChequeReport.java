package com.receipt.receiptPhase.dto.report;

import java.time.LocalDateTime;

public class UndoChequeReport{

    private String cancelledChequeNo;
    private String fullChequeNo;
    private String reason;
    private String userId;
    private LocalDateTime actionDate;

    public UndoChequeReport() {}

    public UndoChequeReport(String cancelledChequeNo, String fullChequeNo, String reason, String userId, LocalDateTime actionDate) {
        this.cancelledChequeNo = cancelledChequeNo;
        this.fullChequeNo = fullChequeNo;
        this.reason = reason;
        this.userId = userId;
        this.actionDate = actionDate;
    }

    // Getters and Setters
    public String getCancelledChequeNo() { return cancelledChequeNo; }
    public void setCancelledChequeNo(String cancelledChequeNo) { this.cancelledChequeNo = cancelledChequeNo; }

    public String getFullChequeNo() { return fullChequeNo; }
    public void setFullChequeNo(String fullChequeNo) { this.fullChequeNo = fullChequeNo; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }
}