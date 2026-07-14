package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.dto.OutstandingRequest;
import com.receipt.receiptPhase.service.OutstandingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OutstandingController {

    private final OutstandingService outstandingService;

    public OutstandingController(OutstandingService outstandingService) {
        this.outstandingService = outstandingService;
    }

    @PostMapping("/receiptCheckOutstanding")
    public ResponseEntity<List<Map<String, Object>>> getOutstandingRecords(@RequestBody OutstandingRequest request) {
        List<Map<String, Object>> result = outstandingService.getOutstandingData(request);
        return ResponseEntity.ok(result);
    }
}