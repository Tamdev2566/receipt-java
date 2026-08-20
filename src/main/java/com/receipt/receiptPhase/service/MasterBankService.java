package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.MasterBankModel;
import com.receipt.receiptPhase.repository.MasterBankRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterBankService {

  @Autowired
  private MasterBankRepository repository;

  private String getCurrentDateTime() {
    return LocalDateTime.now().format(
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );
  }

  public String createBank(MasterBankModel bank, String locationId) {
    bank.setOfficeCode(requiredLocation(locationId));

    String generatedId =
      "BNK" +
      String.format("%04d", Math.abs(System.currentTimeMillis() % 10000));
    bank.setBankId(generatedId);

    bank.setDateCreated(getCurrentDateTime());
    if (bank.getIsValid() == null) bank.setIsValid("1");

    int result = repository.save(bank);
    return result > 0
      ? "Bank created successfully with ID: " + generatedId
      : "Failed to create bank";
  }

  public List<MasterBankModel> getAllBanks(String locationId) {
    return repository.getAllBanksData(requiredLocation(locationId));
  }

  public MasterBankModel getBankById(String bankId, String locationId) {
    return repository.findById(bankId, requiredLocation(locationId));
  }

  public String updateBank(
    String bankId,
    MasterBankModel bank,
    String locationId
  ) {
    bank.setOfficeCode(requiredLocation(locationId));
    bank.setBankId(bankId);
    bank.setDateModified(getCurrentDateTime());

    int result = repository.update(bank);
    return result > 0
      ? "Bank updated successfully"
      : "Bank not found or update failed";
  }

  public String deleteBank(String bankId, String userId, String locationId) {
    String currentDateTime = getCurrentDateTime();
    int result = repository.delete(
      bankId,
      userId,
      currentDateTime,
      requiredLocation(locationId)
    );
    return result > 0
      ? "Bank deleted successfully"
      : "Bank not found or delete failed";
  }

  private String requiredLocation(String locationId) {
    if (
      locationId == null || locationId.isBlank()
    ) throw new IllegalArgumentException("locationId is required.");
    return locationId.trim();
  }
}
