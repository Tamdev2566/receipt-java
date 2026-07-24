package com.receipt.receiptPhase.controller.report;

import com.receipt.receiptPhase.dto.report.RemovedInvoiceReport;
import com.receipt.receiptPhase.service.report.RemovedInvoiceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/reports/removed-invoice")
public class RemovedInvoiceReportController {

    @Autowired
    private RemovedInvoiceReportService removedInvoiceReportService;

    @GetMapping("/getdata")
    public ResponseEntity<List<RemovedInvoiceReport>> getReportData(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<RemovedInvoiceReport> data = removedInvoiceReportService.getRemovedInvoiceReport(fromDate, toDate);
        return ResponseEntity.ok(data);
    }


    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadCsv(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        ByteArrayInputStream in = removedInvoiceReportService.generateCsvReport(fromDate, toDate);

        String currentTimestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String filename = "RemovedInvoiceReport_" + currentTimestamp + ".csv";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(in));
    }
}