package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.service.UpdateTTRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tt-ref")
public class UpdateTTRefController {

    @Autowired
    private UpdateTTRefService ttRefService;


    @GetMapping("/search")
    public ResponseEntity<?> searchTT(@RequestParam String ttNo) {
        Map<String, Object> result = ttRefService.findByTTNo(ttNo);

        if (result == null) {
            return ResponseEntity.ok(Map.of("message", "No record found"));
        }

        return ResponseEntity.ok(result);
    }



    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateTT(@RequestBody Map<String, String> payload) {
        ttRefService.updateTTNo(
                payload.get("originalTTNo"),
                payload.get("newTTNo"),
                payload.get("transactionNo"),
                payload.get("remark"),
                payload.get("userId")
        );

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "T/T Reference Number Updated Successfully");

        return ResponseEntity.ok(response);
    }
}
