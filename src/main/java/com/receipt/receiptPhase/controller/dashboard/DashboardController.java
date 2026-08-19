package com.receipt.receiptPhase.controller.dashboard;

import com.receipt.receiptPhase.service.dashboard.DashboardService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

  @Autowired
  private DashboardService dashboardService;

  @GetMapping("/kpi")
  public ResponseEntity<Map<String, Object>> getDashboardKPIs(
    @RequestParam String locationId
  ) {
    Map<String, Object> kpiData = dashboardService.getDashboardKPIs(locationId);
    return ResponseEntity.ok(kpiData);
  }

  @GetMapping("/recent-receipts")
  public ResponseEntity<List<Map<String, Object>>> getRecentReceipts(
    @RequestParam String locationId
  ) {
    List<Map<String, Object>> receipts = dashboardService.getRecentReceipts(
      locationId
    );
    return ResponseEntity.ok(receipts);
  }

  @GetMapping("/receiptSummary")
  public ResponseEntity<Map<String, Object>> getReceiptSummary(
    @RequestParam String locationId
  ) {
    Map<String, Object> summaryData = dashboardService.getReceiptSummary(
      locationId
    );
    return ResponseEntity.ok(summaryData);
  }
}
