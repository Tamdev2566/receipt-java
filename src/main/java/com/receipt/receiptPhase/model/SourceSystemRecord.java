package com.receipt.receiptPhase.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "source_system_records")
public class SourceSystemRecord {

    @Id
    @Column(name = "bl_no")
    private String blNo;

    @Column(name = "transaction_date")
    private String transactionDate;

    @Column(name = "office_code")
    private String officeCode;

    @Column(name = "vessel_code")
    private String vesselCode;

    @Column(name = "vessel_name")
    private String vesselName;

    @Column(name = "voyage_no")
    private String voyageNo;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "type")
    private String type;

    @Column(name = "reference_date")
    private String referenceDate;

    @Column(name = "reference_no")
    private String referenceNo;

    @Column(name = "value_doc")
    private BigDecimal valueDoc;

    @Column(name = "value_dual")
    private BigDecimal valueDual;

    @Column(name = "original_sgd")
    private BigDecimal originalSgd;

    @Column(name = "original_usd")
    private BigDecimal originalUsd;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @Column(name = "indicator")
    private Integer indicator;

    @Column(name = "date_modified")
    private String dateModified;

    @Column(name = "source")
    private String source;

    // ==========================================
    //          GETTERS AND SETTERS
    // ==========================================

    public String getBlNo() { return blNo; }
    public void setBlNo(String blNo) { this.blNo = blNo; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public String getOfficeCode() { return officeCode; }
    public void setOfficeCode(String officeCode) { this.officeCode = officeCode; }

    public String getVesselCode() { return vesselCode; }
    public void setVesselCode(String vesselCode) { this.vesselCode = vesselCode; }

    public String getVesselName() { return vesselName; }
    public void setVesselName(String vesselName) { this.vesselName = vesselName; }

    public String getVoyageNo() { return voyageNo; }
    public void setVoyageNo(String voyageNo) { this.voyageNo = voyageNo; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReferenceDate() { return referenceDate; }
    public void setReferenceDate(String referenceDate) { this.referenceDate = referenceDate; }

    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

    public BigDecimal getValueDoc() { return valueDoc; }
    public void setValueDoc(BigDecimal valueDoc) { this.valueDoc = valueDoc; }

    public BigDecimal getValueDual() { return valueDual; }
    public void setValueDual(BigDecimal valueDual) { this.valueDual = valueDual; }

    public BigDecimal getOriginalSgd() { return originalSgd; }
    public void setOriginalSgd(BigDecimal originalSgd) { this.originalSgd = originalSgd; }

    public BigDecimal getOriginalUsd() { return originalUsd; }
    public void setOriginalUsd(BigDecimal originalUsd) { this.originalUsd = originalUsd; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public Integer getIndicator() { return indicator; }
    public void setIndicator(Integer indicator) { this.indicator = indicator; }

    public String getDateModified() { return dateModified; }
    public void setDateModified(String dateModified) { this.dateModified = dateModified; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}