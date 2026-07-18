package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.service.UpdateTTRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tt-ref")
public class UpdateTTRefController {

    @Autowired
    private UpdateTTRefService ttRefService;


    @GetMapping("/search")
    public ResponseEntity<?> searchTT(@RequestParam String ttNo) {
        return ResponseEntity.ok(ttRefService.findByTTNo(ttNo));
    }


    @PostMapping("/update")
    public ResponseEntity<String> updateTT(@RequestBody Map<String, String> payload) {
        ttRefService.updateTTNo(
                payload.get("originalTTNo"),
                payload.get("newTTNo"),
                payload.get("transactionNo"),
                payload.get("remark"),
                payload.get("userId")
        );
        return ResponseEntity.ok("T/T Reference Number Updated Successfully");
    }
}
