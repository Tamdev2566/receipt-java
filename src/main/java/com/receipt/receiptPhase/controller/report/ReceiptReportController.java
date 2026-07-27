package com.receipt.receiptPhase.controller.report;

import com.receipt.receiptPhase.dto.report.ReceiptReport;
import com.receipt.receiptPhase.repository.ReceiptReportRepository.ReceiptWithCustomerProjection;
import com.receipt.receiptPhase.service.report.ReceiptReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@CrossOrigin(origins = "*")
public class ReceiptReportController {

    private final ReceiptReportService receiptService;

    public ReceiptReportController(ReceiptReportService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/getReports")
    public ResponseEntity<List<ReceiptWithCustomerProjection>> filterReceipts(@RequestBody ReceiptReport filterDTO) {
        List<ReceiptWithCustomerProjection> result = receiptService.getFilteredReceipts(filterDTO);
        return ResponseEntity.ok(result);
    }
}