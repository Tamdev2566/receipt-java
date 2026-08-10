package com.receipt.receiptPhase.model;

import lombok.Data;

@Data
public class RetrieveRequest {
    private String invoiceNo;
    private String blNo;
    private String vesselName;
    private String voyageNo;
    private String customerName;
}