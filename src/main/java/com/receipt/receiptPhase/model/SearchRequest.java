package com.receipt.receiptPhase.model;

public class SearchRequest {
    private String customerName;
    private String search;
    private String vessel;

    // Getters and Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }

    public String getVessel() { return vessel; }
    public void setVessel(String vessel) { this.vessel = vessel; }
}