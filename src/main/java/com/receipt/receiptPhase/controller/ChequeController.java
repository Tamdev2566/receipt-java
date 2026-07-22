package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.dto.ChequeRequest;
import com.receipt.receiptPhase.service.ChequeReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cheque")
public class ChequeController {

    @Autowired
    private ChequeReaderService chequeService;

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveCheque(@RequestBody ChequeRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String responseMessage = chequeService.processAndSaveCheque(request);
            response.put("status", "SUCCESS");
            response.put("message", responseMessage);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("status", "FAILED");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}