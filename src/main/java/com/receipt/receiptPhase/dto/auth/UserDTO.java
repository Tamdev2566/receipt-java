package com.receipt.receiptPhase.dto.auth;

public class UserDTO {

    private String userId;
    private String email;
    private String name;
    private String isValid;

    public UserDTO() {}

    public UserDTO(String userId, String email, String name, String isValid) {
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