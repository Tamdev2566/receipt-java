package com.receipt.receiptPhase.dto.report;

public class RemovedInvoiceReport {

    private String removedInvoice;
    private String invoiceSource;
    private String userId;
    private String actionDate;
    private String reason;

    public RemovedInvoiceReport() {}

    public RemovedInvoiceReport(String removedInvoice, String invoiceSource, String userId, String actionDate, String reason) {
        this.removedInvoice = removedInvoice;
        this.invoiceSource = invoiceSource;
        this.userId = userId;
        this.actionDate = actionDate;
        this.reason = reason;
    }

    // Getters and Setters
    public String getRemovedInvoice() { return removedInvoice; }
    public void setRemovedInvoice(String removedInvoice) { this.removedInvoice = removedInvoice; }

    public String getInvoiceSource() { return invoiceSource; }
    public void setInvoiceSource(String invoiceSource) { this.invoiceSource = invoiceSource; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActionDate() { return actionDate; }
    public void setActionDate(String actionDate) { this.actionDate = actionDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}