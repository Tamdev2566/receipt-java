package com.receipt.receiptPhase.dto;

public class ChequeRequest {
    private String uid;
    private String fullChequeNo;
    private String chequeNo;
    private String boundOption;
    private boolean autoRead;

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFullChequeNo() { return fullChequeNo; }
    public void setFullChequeNo(String fullChequeNo) { this.fullChequeNo = fullChequeNo; }

    public String getChequeNo() { return chequeNo; }
    public void setChequeNo(String chequeNo) { this.chequeNo = chequeNo; }

    public String getBoundOption() { return boundOption; }
    public void setBoundOption(String boundOption) { this.boundOption = boundOption; }

    public boolean isAutoRead() { return autoRead; }
    public void setAutoRead(boolean autoRead) { this.autoRead = autoRead; }
}