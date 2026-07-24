package com.receipt.receiptPhase.dto.ediCoda;

import java.math.BigDecimal;

public class EdiToCoda {

    private String referenceNo;
    private String customerName;
    private String currency;
    private BigDecimal amount;

    public EdiToCoda() {}

    public EdiToCoda(String referenceNo, String customerName, String currency, BigDecimal amount) {
        this.referenceNo = referenceNo;
        this.customerName = customerName;
        this.currency = currency;
        this.amount = amount;
    }


    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}