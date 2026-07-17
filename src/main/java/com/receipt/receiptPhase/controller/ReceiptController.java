package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmReceipt(@RequestBody ReceiptModal receipt) {
        String transactionNo = receiptService.saveReceipt(receipt);
        return ResponseEntity.ok("Receipt saved successfully. Transaction No: " + transactionNo);
    }

    @GetMapping("/retrive")
    public ResponseEntity<?> getAllReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }
}