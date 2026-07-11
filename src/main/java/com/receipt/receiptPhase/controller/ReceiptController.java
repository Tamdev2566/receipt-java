package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.model.DashboardStats;
import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {
    private final ReceiptService service;

    public ReceiptController(ReceiptService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createReceipt(@RequestBody ReceiptModal receipt) {
        ReceiptModal createdReceipt = service.createReceipt(receipt);
        return ResponseEntity.ok(Map.of(
                "message", "Receipt created successfully",
                "transactionNo", createdReceipt.getTransactionNo()
        ));
    }

    @GetMapping
    public ResponseEntity<List<ReceiptModal>> getAllReceipts() {
        return ResponseEntity.ok(service.getAllReceipts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceiptModal> updateReceipt(@PathVariable String id, @RequestBody ReceiptModal receipt) {
        return ResponseEntity.ok(service.updateReceipt(id, receipt));
    }

    @PatchMapping("/{id}/undo-verify")
    public ResponseEntity<ReceiptModal> undoVerify(@PathVariable String id) {
        return ResponseEntity.ok(service.undoVerify(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReceiptModal> removeReceipt(@PathVariable String id) {
        return ResponseEntity.ok(service.removeReceipt(id));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(service.getDashboardStats());
    }
}
