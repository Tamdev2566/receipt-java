package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.model.DashboardStats;
import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {
    private final ReceiptService service;

    public ReceiptController(ReceiptService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReceiptModal> createReceipt(@RequestBody ReceiptModal receipt) {
        return ResponseEntity.ok(service.createReceipt(receipt));
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