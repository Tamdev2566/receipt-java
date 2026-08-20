package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.model.MasterAccountModel;
import com.receipt.receiptPhase.service.MasterAccountService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master-accounts")
public class MasterAccountController {

  @Autowired
  private MasterAccountService service;

  @PostMapping("/add")
  public ResponseEntity<Map<String, Object>> addAccount(
    @RequestBody MasterAccountModel account,
    @RequestParam String locationId
  ) {
    Map<String, Object> response = new HashMap<>();
    String message = service.createAccount(account, locationId);
    response.put(
      "status",
      message.contains("successfully") ? "SUCCESS" : "FAILED"
    );
    response.put("message", message);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/list")
  public ResponseEntity<Map<String, Object>> getAllAccounts(
    @RequestParam String locationId
  ) {
    Map<String, Object> response = new HashMap<>();
    List<MasterAccountModel> accounts = service.getAllAccounts(locationId);
    response.put("status", "SUCCESS");
    response.put("totalRecords", accounts.size());
    response.put("data", accounts);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{accountId}")
  public ResponseEntity<Map<String, Object>> getAccountById(
    @PathVariable String accountId,
    @RequestParam String locationId
  ) {
    Map<String, Object> response = new HashMap<>();
    MasterAccountModel account = service.getAccountById(accountId, locationId);
    if (account != null) {
      response.put("status", "SUCCESS");
      response.put("data", account);
    } else {
      response.put("status", "FAILED");
      response.put("message", "Account Not Found");
    }
    return ResponseEntity.ok(response);
  }

  @PutMapping("/update/{accountId}")
  public ResponseEntity<Map<String, Object>> updateAccount(
    @PathVariable String accountId,
    @RequestBody MasterAccountModel account,
    @RequestParam String locationId
  ) {
    Map<String, Object> response = new HashMap<>();
    String message = service.updateAccount(accountId, account, locationId);
    response.put(
      "status",
      message.contains("successfully") ? "SUCCESS" : "FAILED"
    );
    response.put("message", message);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/delete/{accountId}")
  public ResponseEntity<Map<String, Object>> deleteAccount(
    @PathVariable String accountId,
    @RequestParam(required = false, defaultValue = "SYSTEM") String userId,
    @RequestParam String locationId
  ) {
    Map<String, Object> response = new HashMap<>();
    String message = service.deleteAccount(accountId, userId, locationId);
    response.put(
      "status",
      message.contains("successfully") ? "SUCCESS" : "FAILED"
    );
    response.put("message", message);
    return ResponseEntity.ok(response);
  }
}
