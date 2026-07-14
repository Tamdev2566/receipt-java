package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.dto.RemoveInvoiceRequest;
import com.receipt.receiptPhase.service.RemoveInvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RemoveInvoiceController {

    private final RemoveInvoiceService removeInvoiceService;

    public RemoveInvoiceController(RemoveInvoiceService removeInvoiceService) {
        this.removeInvoiceService = removeInvoiceService;
    }

    @PostMapping("/receiptRemoveInvoice")
    public ResponseEntity<Map<String, String>> removeInvoice(@RequestBody RemoveInvoiceRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            removeInvoiceService.removeInvoices(request);
            response.put("status", "success");
            response.put("message", "Successfully removed Invoice no!");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("status", "warning");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred during removal: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}