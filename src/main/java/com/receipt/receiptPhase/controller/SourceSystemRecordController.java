package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.dto.SearchRequest;
import com.receipt.receiptPhase.repository.VesselProjection;
import com.receipt.receiptPhase.repository.VoyageProjection;
import com.receipt.receiptPhase.service.SourceSystemRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SourceSystemRecordController {

    private final SourceSystemRecordService service;

    public SourceSystemRecordController(SourceSystemRecordService service) {
        this.service = service;
    }

    @PostMapping("/getvessel")
    public ResponseEntity<List<VesselProjection>> getVessel(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(service.getVessels(request));
    }

    @PostMapping("/getvoyage")
    public ResponseEntity<List<VoyageProjection>> getVoyage(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(service.getVoyages(request));
    }
}