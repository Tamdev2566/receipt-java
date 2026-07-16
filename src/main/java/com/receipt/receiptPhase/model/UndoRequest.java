package com.receipt.receiptPhase.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class UndoRequest {

    private String blNo;
    private String vesselName;
    private String voyageNo;
    private String customerName;

    private List<ReceiptDTO> receipts;
    private List<InvoiceDTO> invoices;
    private List<PartialDTO> outstandings;

    public String getBlNo() { return blNo; }
    public void setBlNo(String blNo) { this.blNo = blNo; }

    public String getVesselName() { return vesselName; }
    public void setVesselName(String vesselName) { this.vesselName = vesselName; }

    public String getVoyageNo() { return voyageNo; }
    public void setVoyageNo(String voyageNo) { this.voyageNo = voyageNo; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<ReceiptDTO> getReceipts() { return receipts; }
    public void setReceipts(List<ReceiptDTO> receipts) { this.receipts = receipts; }

    public List<InvoiceDTO> getInvoices() { return invoices; }
    public void setInvoices(List<InvoiceDTO> invoices) { this.invoices = invoices; }

    public List<PartialDTO> getOutstandings() { return outstandings; }
    public void setOutstandings(List<PartialDTO> outstandings) { this.outstandings = outstandings; }



    public static class ReceiptDTO {
        public String transactionNo;
        public String transactionDate;
        public String receiptDate;
        public String referenceNo;
        public String currency;
        public BigDecimal amount;
        public BigDecimal paidInvoiceTotal;
    }

    public static class InvoiceDTO {
        public String transactionNo;
        public String type;
        public String referenceNo;
        public String currency;
        public BigDecimal settlementAmt;
        public BigDecimal sgdAmount;
        public BigDecimal usdAmount;
        public BigDecimal originalsgdAmount;
        public BigDecimal originalusdAmount;
        public BigDecimal partial;
        public BigDecimal writeOff;
    }

    public static class PartialDTO {
        public String transactionNo;
        public String type;
        public String referenceNo;
        public String currency;
        public BigDecimal settlementAmt;
        public BigDecimal sgdAmount;
        public BigDecimal usdAmount;
    }
}