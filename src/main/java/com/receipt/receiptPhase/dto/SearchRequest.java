package com.receipt.receiptPhase.dto;

public class SearchRequest {
    private String customerName;
    private String search;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
}