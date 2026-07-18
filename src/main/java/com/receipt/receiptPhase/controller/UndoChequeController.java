package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.service.UndoChequeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/undoCheque")
public class UndoChequeController {

    @Autowired
    private UndoChequeService undoChequeService;


    @GetMapping("/search")
    public ResponseEntity<?> searchCheque(@RequestParam String chequeNo, @RequestParam String fullChequeNo) {
        Map<String, Object> details = undoChequeService.getChequeDetails(chequeNo, fullChequeNo);
        if (details.isEmpty()) {
            return ResponseEntity.status(404).body("No records found.");
        }
        return ResponseEntity.ok(details);
    }


    @PostMapping("/undo")
    public ResponseEntity<Map<String, String>> undoCheque(@RequestBody Map<String, Object> request) {
        String chequeNo = (String) request.get("chequeNo");
        String fullChequeNo = (String) request.get("fullChequeNo");
        String remark = (String) request.get("remark");
        String userId = (String) request.get("userId");

        undoChequeService.undoCheque(chequeNo, fullChequeNo, remark, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "This Cheque No was successfully Cancelled.");
        return ResponseEntity.ok(response);
    }
}