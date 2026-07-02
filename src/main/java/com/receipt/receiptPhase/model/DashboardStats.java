package com.receipt.receiptPhase.model;

public class DashboardStats {
    private long totalReceipts;
    private long activeReceipts;
    private long undoVerifiedReceipts;
    private long removedReceipts;
    private double totalAmount;

    public DashboardStats(long totalReceipts, long activeReceipts, long undoVerifiedReceipts, long removedReceipts, double totalAmount) {
        this.totalReceipts = totalReceipts;
        this.activeReceipts = activeReceipts;
        this.undoVerifiedReceipts = undoVerifiedReceipts;
        this.removedReceipts = removedReceipts;
        this.totalAmount = totalAmount;
    }

    // Getters
    public long getTotalReceipts() { return totalReceipts; }
    public long getActiveReceipts() { return activeReceipts; }
    public long getUndoVerifiedReceipts() { return undoVerifiedReceipts; }
    public long getRemovedReceipts() { return removedReceipts; }
    public double getTotalAmount() { return totalAmount; }
}