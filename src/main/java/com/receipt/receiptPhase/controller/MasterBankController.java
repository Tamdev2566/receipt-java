package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.model.MasterBankModel;
import com.receipt.receiptPhase.service.MasterBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/master-banks")
public class MasterBankController {

    @Autowired
    private MasterBankService service;


    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addBank(@RequestBody MasterBankModel bank) {
        Map<String, Object> response = new HashMap<>();
        String message = service.createBank(bank);
        response.put("status", message.contains("successfully") ? "SUCCESS" : "FAILED");
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllBanks(@RequestParam(required = false, defaultValue = "true") boolean isValid) {
        Map<String, Object> response = new HashMap<>();
        List<MasterBankModel> banks = service.getAllBanks(isValid);

        response.put("status", "SUCCESS");
        response.put("totalRecords", banks.size());
        response.put("isValidFilter", isValid);
        response.put("data", banks);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{bankId}")
    public ResponseEntity<Map<String, Object>> getBankById(@PathVariable String bankId) {
        Map<String, Object> response = new HashMap<>();
        MasterBankModel bank = service.getBankById(bankId);
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
    public ResponseEntity<Map<String, Object>> updateBank(@PathVariable String bankId, @RequestBody MasterBankModel bank) {
        Map<String, Object> response = new HashMap<>();
        String message = service.updateBank(bankId, bank);
        response.put("status", message.contains("successfully") ? "SUCCESS" : "FAILED");
        response.put("message", message);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{bankId}")
    public ResponseEntity<Map<String, Object>> deleteBank(@PathVariable String bankId, @RequestParam(required = false, defaultValue = "SYSTEM") String userId) {
        Map<String, Object> response = new HashMap<>();
        String message = service.deleteBank(bankId, userId);
        response.put("status", message.contains("successfully") ? "SUCCESS" : "FAILED");
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}