package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(receiptService.confirmPayment(request));
    }

    @PostMapping("/over-payment")
    public ResponseEntity<Map<String, Object>> overPayment(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(receiptService.overPayment(request));
    }

    @GetMapping("/retrive")
    public ResponseEntity<Iterable<ReceiptModal>> getAllReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }
}
