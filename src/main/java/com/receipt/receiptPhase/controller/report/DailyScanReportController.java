package com.receipt.receiptPhase.controller.report;

import com.receipt.receiptPhase.dto.report.DailyScanReport;
import com.receipt.receiptPhase.service.report.DailyScanReportService;
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
@RequestMapping("/api/reports/daily-scan")
public class DailyScanReportController {

    @Autowired
    private DailyScanReportService dailyScanReportService;


    @GetMapping("/getdata")
    public ResponseEntity<List<DailyScanReport>> getReportData(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(value = "bound", defaultValue = "ALL") String bound) {

        List<DailyScanReport> data = dailyScanReportService.getDailyScanReport(fromDate, toDate, bound);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadCsv(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(value = "bound", defaultValue = "ALL") String bound) {

        ByteArrayInputStream in = dailyScanReportService.generateCsvReport(fromDate, toDate, bound);

        String currentTimestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String filename = "DailyScanReport_" + currentTimestamp + ".csv";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(in));
    }
}