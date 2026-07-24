package com.receipt.receiptPhase.controller.ediCoda;



import com.receipt.receiptPhase.dto.ediCoda.EdiToCoda;
import com.receipt.receiptPhase.service.ediCoda.EdiToCodaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ediCoda")
public class EdiToCodaController {

    @Autowired
    private EdiToCodaService ediReceiptService;

    @GetMapping("/retrieve")
    public ResponseEntity<List<EdiToCoda>> retrieveReceipts(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<EdiToCoda> data = ediReceiptService.retrieveReceipts(fromDate, toDate);
        return ResponseEntity.ok(data);
    }


    @PostMapping("/export")
    public ResponseEntity<Map<String, Object>> exportReceipts(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        String resultMessage = ediReceiptService.exportEdiReceipts(fromDate, toDate);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", resultMessage);

        return ResponseEntity.ok(response);
    }
}