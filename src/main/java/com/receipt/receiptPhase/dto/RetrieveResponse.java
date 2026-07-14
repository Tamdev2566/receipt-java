package com.receipt.receiptPhase.dto;

import java.util.List;
import java.util.Map;

public class RetrieveResponse {
    private boolean success;
    private String message;
    private Map<String, Object> headerData;
    private List<Map<String, Object>> receipts;
    private List<Map<String, Object>> invoices;
    private List<Map<String, Object>> outstandings;

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getHeaderData() {
        return headerData;
    }
    public void setHeaderData(Map<String, Object> headerData) {
        this.headerData = headerData;
    }

    public List<Map<String, Object>> getReceipts() {
        return receipts;
    }
    public void setReceipts(List<Map<String, Object>> receipts) {
        this.receipts = receipts;
    }

    public List<Map<String, Object>> getInvoices() {
        return invoices;
    }
    public void setInvoices(List<Map<String, Object>> invoices) {
        this.invoices = invoices;
    }

    public List<Map<String, Object>> getOutstandings() {
        return outstandings;
    }
    public void setOutstandings(List<Map<String, Object>> outstandings) {
        this.outstandings = outstandings;
    }
}