package com.receipt.receiptPhase.model;

import lombok.Data;

@Data
public class RetrieveRequest {

  private String locationId;
  private String officeCode;
  private String invoiceNo;
  private String blNo;
  private String vesselName;
  private String voyageNo;
  private String customerName;

  public String getLocationId() {
    return locationId;
  }

  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  public String getOfficeCode() {
    return officeCode;
  }

  public void setOfficeCode(String officeCode) {
    this.officeCode = officeCode;
  }

  public String getLocationCode() {
    return locationId != null && !locationId.isBlank()
      ? locationId.trim()
      : officeCode != null
        ? officeCode.trim()
        : "";
  }
}
