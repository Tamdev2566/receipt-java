package com.receipt.receiptPhase.controller.receipt;

import com.receipt.receiptPhase.model.RetrieveRequest;
import com.receipt.receiptPhase.model.RetrieveResponse;
import com.receipt.receiptPhase.service.RetrieveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RetrieveController {

    private final RetrieveService retrieveAllService;

    public RetrieveController(RetrieveService retrieveAllService) {
        this.retrieveAllService = retrieveAllService;
    }

    @PostMapping("/receiptRetrieve")
    public ResponseEntity<RetrieveResponse> retrieveAll(@RequestBody RetrieveRequest request) {
        RetrieveResponse response = retrieveAllService.retrieveAllData(request);
        return ResponseEntity.ok(response);
    }
}