package com.receipt.receiptPhase.controller.report;

import com.receipt.receiptPhase.dto.report.AgingReport;
import com.receipt.receiptPhase.service.report.AgingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/reports/aging")
public class AgingReportController {

    @Autowired
    private AgingReportService agingReportService;

    @GetMapping("/getdata")
    public ResponseEntity<List<AgingReport>> getReportData(@RequestParam("days") int days) {
        List<AgingReport> data = agingReportService.getAgingReport(days);
        return ResponseEntity.ok(data);
    }


    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadCsv(@RequestParam("days") int days) {

        ByteArrayInputStream in = agingReportService.generateCsvReport(days);

        String currentTimestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String filename = "Missing_ChequeNo_" + currentTimestamp + ".csv";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(in));
    }
}