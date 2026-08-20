package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.model.MasterBankModel;
import com.receipt.receiptPhase.service.MasterBankService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master-banks")
public class MasterBankController {

  @Autowired
  private MasterBankService service;

  @PostMapping("/add")
  public ResponseEntity<Map<String, Object>> addBank(
    @RequestBody MasterBankModel bank,
    @RequestParam String locationId
  ) {
    Map<String, Object> response = new HashMap<>();
    String message = service.createBank(bank, locationId);
    response.put(
      "status",
      message.contains("successfully") ? "SUCCESS" : "FAILED"
    );
    response.put("message", message);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/list")
  public ResponseEntity<Map<String, Object>> getAllBanks(
    @RequestParam String locationId
  ) {
    Map<String, Object> response = new HashMap<>();
    try {
      List<MasterBankModel> banks = service.getAllBanks(locationId);

      response.put("status", "SUCCESS");
      response.put("totalRecords", banks.size());
      response.put("data", banks);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("status", "ERROR");
      response.put("message", "Failed to retrieve records: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        response
      );
    }
  }

  @GetMapping("/{bankId}")
  public ResponseEntity<Map<String, Object>> getBankById(
    @PathVariable String bankId,
    @RequestParam String locationId
  ) {
    Map<String, Object> response = new HashMap<>();
    MasterBankModel bank = service.getBankById(bankId, locationId);
    if (bank != null) {
      response.put("status", "SUCCESS");
      response.put("data", bank);
    } else {
      response.put("status", "FAILED");
      response.put("message", "Bank Not Found");
    }
    return ResponseEntity.ok(response);
  }

  @PutMapping("/update/{bankId}")
  public ResponseEntity<Map<String, Object>> updateBank(
    @PathVariable String bankId,
    @RequestBody MasterBankModel bank,
    @RequestParam String locationId
  ) {
    Map<String, Object> response = new HashMap<>();
    String message = service.updateBank(bankId, bank, locationId);
    response.put(
      "status",
      message.contains("successfully") ? "SUCCESS" : "FAILED"
    );
    response.put("message", message);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/delete/{bankId}")
  public ResponseEntity<Map<String, Object>> deleteBank(
    @PathVariable String bankId,
    @RequestParam(required = false, defaultValue = "SYSTEM") String userId,
    @RequestParam String locationId
  ) {
    Map<String, Object> response = new HashMap<>();
    String message = service.deleteBank(bankId, userId, locationId);
    response.put(
      "status",
      message.contains("successfully") ? "SUCCESS" : "FAILED"
    );
    response.put("message", message);
    return ResponseEntity.ok(response);
  }
}
