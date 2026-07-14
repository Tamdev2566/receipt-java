package com.receipt.receiptPhase.dto;

import java.util.List;

public class UndoRequest {
    private List<UndoReceiptItem> receipts;

    public List<UndoReceiptItem> getReceipts() { return receipts; }
    public void setReceipts(List<UndoReceiptItem> receipts) { this.receipts = receipts; }

    public static class UndoReceiptItem {
        private String transactionNo;
        private Double amount;
        private String currency;

        public String getTransactionNo() { return transactionNo; }
        public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}