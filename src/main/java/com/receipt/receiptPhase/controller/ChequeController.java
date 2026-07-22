package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.dto.ChequeRequest;
import com.receipt.receiptPhase.service.ChequeReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cheque")
public class ChequeController {

    @Autowired
    private ChequeReaderService chequeService;

    @PostMapping("/save")
    public ResponseEntity<?> saveCheque(@RequestBody ChequeRequest request) {
        try {
            String responseMessage = chequeService.processAndSaveCheque(request);
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}