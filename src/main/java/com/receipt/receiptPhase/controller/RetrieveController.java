package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.model.RetrieveRequest;
import com.receipt.receiptPhase.model.RetrieveResponse;
import com.receipt.receiptPhase.service.RetrieveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RetrieveController {

    private final RetrieveService retrieveService;

    public RetrieveController(RetrieveService retrieveService) {
        this.retrieveService = retrieveService;
    }

    @PostMapping("/receiptRetrieve")
    public ResponseEntity<RetrieveResponse> retrieveRecords(@RequestBody RetrieveRequest request) {
        RetrieveResponse response = retrieveService.retrieveData(request);
        return ResponseEntity.ok(response);
    }
}