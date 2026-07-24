package com.receipt.receiptPhase.dto.report;

public class AgingReport{

    private String bound;
    private String fullChequeNo;
    private String chequeNo;
    private String bankName;
    private String scanUserId;
    private String createTime;
    private Integer aging;
    private String autoRead;

    public AgingReport() {}

    public AgingReport(String bound, String fullChequeNo, String chequeNo, String bankName, String scanUserId, String createTime, Integer aging, String autoRead) {
        this.bound = bound;
        this.fullChequeNo = fullChequeNo;
        this.chequeNo = chequeNo;
        this.bankName = bankName;
        this.scanUserId = scanUserId;
        this.createTime = createTime;
        this.aging = aging;
        this.autoRead = autoRead;
    }

    // Getters and Setters
    public String getBound() { return bound; }
    public void setBound(String bound) { this.bound = bound; }

    public String getFullChequeNo() { return fullChequeNo; }
    public void setFullChequeNo(String fullChequeNo) { this.fullChequeNo = fullChequeNo; }

    public String getChequeNo() { return chequeNo; }
    public void setChequeNo(String chequeNo) { this.chequeNo = chequeNo; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getScanUserId() { return scanUserId; }
    public void setScanUserId(String scanUserId) { this.scanUserId = scanUserId; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public Integer getAging() { return aging; }
    public void setAging(Integer aging) { this.aging = aging; }

    public String getAutoRead() { return autoRead; }
    public void setAutoRead(String autoRead) { this.autoRead = autoRead; }
}