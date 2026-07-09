package com.receipt.receiptPhase.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "receipt")
public class ReceiptModal {

    @Id
    @Column(name = "transaction_no", length = 20)
    private String transactionNo;

    @Column(name = "transaction_date", length = 20)
    private String transactionDate;

    @Column(name = "office_code", length = 10)
    private String officeCode;

    @Column(name = "payment_mode", length = 10)
    private String paymentMode;

    @Column(name = "receipt_date", length = 20)
    private String receiptDate;

    @Column(name = "reference_no", length = 20)
    private String referenceNo;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "amount", precision = 18, scale = 4)
    private BigDecimal amount;

    @Column(name = "bank_charge", precision = 18, scale = 0)
    private BigDecimal bankCharge;

    @Column(name = "paid_invoice_total", precision = 18, scale = 4)
    private BigDecimal paidInvoiceTotal;

    @Column(name = "receipt_total", precision = 18, scale = 4)
    private BigDecimal receiptTotal;

    @Column(name = "balance_amount", precision = 18, scale = 4)
    private BigDecimal balanceAmount;

    @Column(name = "posted_to_coda")
    private Boolean postedToCoda;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "bank", length = 20)
    private String bank;

    @Column(name = "created_date", length = 20)
    private String createdDate;

    @Column(name = "created_user", length = 30)
    private String createdUser;

    @Column(name = "modified_date", length = 20)
    private String modifiedDate;

    @Column(name = "modified_user", length = 30)
    private String modifiedUser;

    public ReceiptModal() {}

    // Getters and Setters
    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public String getOfficeCode() { return officeCode; }
    public void setOfficeCode(String officeCode) { this.officeCode = officeCode; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getReceiptDate() { return receiptDate; }
    public void setReceiptDate(String receiptDate) { this.receiptDate = receiptDate; }

    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getBankCharge() { return bankCharge; }
    public void setBankCharge(BigDecimal bankCharge) { this.bankCharge = bankCharge; }

    public BigDecimal getPaidInvoiceTotal() { return paidInvoiceTotal; }
    public void setPaidInvoiceTotal(BigDecimal paidInvoiceTotal) { this.paidInvoiceTotal = paidInvoiceTotal; }

    public BigDecimal getReceiptTotal() { return receiptTotal; }
    public void setReceiptTotal(BigDecimal receiptTotal) { this.receiptTotal = receiptTotal; }

    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }

    public Boolean getPostedToCoda() { return postedToCoda; }
    public void setPostedToCoda(Boolean postedToCoda) { this.postedToCoda = postedToCoda; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getCreatedUser() { return createdUser; }
    public void setCreatedUser(String createdUser) { this.createdUser = createdUser; }

    public String getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(String modifiedDate) { this.modifiedDate = modifiedDate; }

    public String getModifiedUser() { return modifiedUser; }
    public void setModifiedUser(String modifiedUser) { this.modifiedUser = modifiedUser; }
}