package com.receipt.receiptPhase.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserModal {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "is_valid")
    private String isValid;

    public UserModal() {}

    public UserModal(String userId, String isValid) {
        this.userId = userId;
        this.isValid = isValid;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getIsValid() { return isValid; }
    public void setIsValid(String isValid) { this.isValid = isValid; }
}