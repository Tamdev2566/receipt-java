package com.receipt.receiptPhase.model;

import java.time.LocalDateTime;

public class ChequeReaderModel {
    private Long id;
    private String bound;
    private String chequeNo;
    private String bankName;
    private String scanUserId;
    private LocalDateTime lastModified;
    private LocalDateTime createTime;
    private boolean autoRead;
    private String fullChequeNo;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBound() { return bound; }
    public void setBound(String bound) { this.bound = bound; }

    public String getChequeNo() { return chequeNo; }
    public void setChequeNo(String chequeNo) { this.chequeNo = chequeNo; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getScanUserId() { return scanUserId; }
    public void setScanUserId(String scanUserId) { this.scanUserId = scanUserId; }

    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public boolean isAutoRead() { return autoRead; }
    public void setAutoRead(boolean autoRead) { this.autoRead = autoRead; }

    public String getFullChequeNo() { return fullChequeNo; }
    public void setFullChequeNo(String fullChequeNo) { this.fullChequeNo = fullChequeNo; }
}