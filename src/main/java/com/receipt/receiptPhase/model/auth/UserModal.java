package com.receipt.receiptPhase.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users", schema = "public")
public class UserModal {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "is_valid")
    private String isValid;

    public UserModal() {}

    public UserModal(String userId, String email, String name, String isValid) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.isValid = isValid;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIsValid() { return isValid; }
    public void setIsValid(String isValid) { this.isValid = isValid; }
}