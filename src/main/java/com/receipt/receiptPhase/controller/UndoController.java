package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.model.UndoRequest;
import com.receipt.receiptPhase.service.UndoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/undo-payment")
@CrossOrigin(origins = "*")
public class UndoController {

    @Autowired
    private UndoService undoService;
    @GetMapping("/retrieve")
    public ResponseEntity<Object> retrieveRecords(
            @RequestParam(required = false) String invoiceNo,
            @RequestParam(required = false) String chequeNo,
            @RequestParam(required = false) String blNo) {

        if ((invoiceNo == null || invoiceNo.trim().isEmpty()) &&
                (chequeNo == null || chequeNo.trim().isEmpty()) &&
                (blNo == null || blNo.trim().isEmpty())) {

            Map<String, Object> badRequest = new HashMap<>();
            badRequest.put("status", "VALIDATION_ERROR");
            badRequest.put("message", "Please Enter Invoice No or BL No or Cheque No.");
            return ResponseEntity.badRequest().body(badRequest);
        }

        UndoRequest data = undoService.retrieveRecords(invoiceNo, chequeNo, blNo);

        if (data == null) {

            Map<String, Object> emptyContainer = new HashMap<>();
            emptyContainer.put("status", "NO_RECORDS");
            emptyContainer.put("message", "No records found.");
            return ResponseEntity.ok(emptyContainer);
        }

        return ResponseEntity.ok(data);
    }

    @PutMapping("/execute-rollback")
    public ResponseEntity<Map<String, String>> executeUndo(@RequestBody List<String> selectedTransactionNos) {
        Map<String, String> response = new HashMap<>();

        if (selectedTransactionNos == null || selectedTransactionNos.isEmpty()) {
            response.put("status", "ERROR");
            response.put("message", "Please choose Transaction No to undo payments.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            undoService.processUndoPayment(selectedTransactionNos);
            response.put("status", "SUCCESS");
            response.put("message", "Payment undone successfully..");
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            response.put("status", "FAILED");
            response.put("message", "Rollback failed: " + ex.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}