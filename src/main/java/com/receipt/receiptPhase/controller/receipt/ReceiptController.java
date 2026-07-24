package com.receipt.receiptPhase.controller.receipt;

import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<List<Map<String, Object>>> getAllReceiptsWithActions() {
        return ResponseEntity.ok(receiptService.getAllReceiptsWithActions());
    }
}
