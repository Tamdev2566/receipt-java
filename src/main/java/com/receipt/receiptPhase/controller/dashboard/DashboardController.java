package com.receipt.receiptPhase.controller.dashboard;


import com.receipt.receiptPhase.service.dashboard.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/kpi")
    public ResponseEntity<Map<String, Object>> getDashboardKPIs() {
        Map<String, Object> kpiData = dashboardService.getDashboardKPIs();
        return ResponseEntity.ok(kpiData);
    }

    @GetMapping("/recent-receipts")
    public ResponseEntity<List<Map<String, Object>>> getRecentReceipts() {
        List<Map<String, Object>> receipts = dashboardService.getRecentReceipts();
        return ResponseEntity.ok(receipts);
    }


    @GetMapping("/receiptSummary")
    public ResponseEntity<Map<String, Object>> getReceiptSummary() {
        Map<String, Object> summaryData = dashboardService.getReceiptSummary();
        return ResponseEntity.ok(summaryData);
    }
}