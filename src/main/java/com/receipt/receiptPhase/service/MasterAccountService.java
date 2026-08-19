package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.MasterAccountModel;
import com.receipt.receiptPhase.repository.MasterAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MasterAccountService {

    @Autowired
    private MasterAccountRepository repository;

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String createAccount(MasterAccountModel account, String locationId) {
        account.setOfficeCode(requiredLocation(locationId));
        // Backend-லேயே Account ID உருவாக்குவதற்கான லாஜிக் (varchar(7) க்கு ஏற்றவாறு)
        String generatedId = "ACC" + String.format("%04d", Math.abs(System.currentTimeMillis() % 10000));
        account.setAccountId(generatedId);

        // Date மற்றும் Default values
        account.setDateCreated(getCurrentDateTime());
        if(account.getIsValid() == null) account.setIsValid("1"); // Default

        int result = repository.save(account);
        return result > 0 ? "Account created successfully with ID: " + generatedId : "Failed to create account";
    }

    public List<MasterAccountModel> getAllAccounts(String locationId) {
        return repository.findAll(requiredLocation(locationId));
    }

    public MasterAccountModel getAccountById(String accountId, String locationId) {
        return repository.findById(accountId, requiredLocation(locationId));
    }

    public String updateAccount(String accountId, MasterAccountModel account, String locationId) {
        account.setOfficeCode(requiredLocation(locationId));
        account.setAccountId(accountId);
        account.setDateModified(getCurrentDateTime());

        int result = repository.update(account);
        return result > 0 ? "Account updated successfully" : "Account not found or update failed";
    }

    public String deleteAccount(String accountId, String userId, String locationId) {
        String dateDeleted = getCurrentDateTime();
        int result = repository.delete(accountId, userId, dateDeleted, requiredLocation(locationId));
        return result > 0 ? "Account deleted successfully" : "Account not found or delete failed";
    }

    private String requiredLocation(String locationId) {
        if (locationId == null || locationId.isBlank()) throw new IllegalArgumentException("locationId is required.");
        return locationId.trim();
    }
}
