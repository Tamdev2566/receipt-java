package com.receipt.receiptPhase.model;

import java.util.List;

public class OutstandingRequest {

  private String locationId;
  private String officeCode;
  private List<String> customerNames;
  private String source;
  private String currency;

  // Getters and Setters
  public List<String> getCustomerNames() {
    return customerNames;
  }

  public void setCustomerNames(List<String> customerNames) {
    this.customerNames = customerNames;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

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
