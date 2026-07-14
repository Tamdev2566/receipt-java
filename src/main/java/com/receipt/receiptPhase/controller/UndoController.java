package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.dto.UndoRequest;
import com.receipt.receiptPhase.service.UndoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UndoController {

    private final UndoService undoService;

    public UndoController(UndoService undoService) {
        this.undoService = undoService;
    }

    @PostMapping("/undoReceipt")
    public ResponseEntity<Map<String, String>> undoReceipts(@RequestBody UndoRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            undoService.processUndo(request);
            response.put("status", "success");
            response.put("message", "This Receipt was successfully deleted.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}