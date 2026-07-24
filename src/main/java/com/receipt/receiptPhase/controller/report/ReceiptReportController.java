package com.receipt.receiptPhase.controller.report;

import com.receipt.receiptPhase.dto.report.ReceiptReport;
import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.service.report.ReceiptReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@CrossOrigin(origins = "*")
public class ReceiptReportController {

    @Autowired
    private ReceiptReportService receiptService;

    @PostMapping("/getReports")
    public ResponseEntity<List<ReceiptModal>> filterReceipts(@RequestBody ReceiptReport filterDTO) {
        List<ReceiptModal> result = receiptService.getFilteredReceipts(filterDTO);
        return ResponseEntity.ok(result);
    }
}