package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.dto.ChequeRequest;
import com.receipt.receiptPhase.model.ChequeReaderModel;
import com.receipt.receiptPhase.service.ChequeReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cheque")
public class ChequeController {

    @Autowired
    private ChequeReaderService chequeService;

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveCheque(@RequestBody ChequeRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String responseMessage = chequeService.processAndSaveCheque(request);
            response.put("status", "SUCCESS");
            response.put("message", responseMessage);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("status", "FAILED");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/updateCheque")
    public ResponseEntity<Map<String, Object>> updateCheque(@RequestBody ChequeReaderModel request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String responseMessage = chequeService.updateChequeDetails(request);
            response.put("status", "SUCCESS");
            response.put("message", responseMessage);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("status", "FAILED");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> getChequesList(@RequestBody(required = false) Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        try {
            String uid = (requestData != null) ? requestData.get("uid") : null;
            String locationId = (requestData != null) ? requestData.get("locationId") : null;
            List<ChequeReaderModel> list = chequeService.getChequeList(uid, locationId);

            response.put("status", "SUCCESS");
            response.put("totalRecords", list.size());
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to retrieve records: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
