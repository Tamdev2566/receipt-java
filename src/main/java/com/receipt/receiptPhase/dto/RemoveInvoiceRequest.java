package com.receipt.receiptPhase.dto;

import java.util.List;

public class RemoveInvoiceRequest {
    private String userId;
    private String reason;
    private List<InvoiceItem> invoices;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public List<InvoiceItem> getInvoices() { return invoices; }
    public void setInvoices(List<InvoiceItem> invoices) { this.invoices = invoices; }

    public static class InvoiceItem {
        private String referenceNo;
        private String source;

        // Getters and Setters
        public String getReferenceNo() { return referenceNo; }
        public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }
}