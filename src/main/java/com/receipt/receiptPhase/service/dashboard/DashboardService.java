package com.receipt.receiptPhase.service.dashboard;

import com.receipt.receiptPhase.service.ReceiptService;
import com.receipt.receiptPhase.service.report.AgingReportService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

  @Autowired
  private ReceiptService receiptService;

  @Autowired
  private AgingReportService agingReportService;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Map<String, Object> getDashboardKPIs(String locationId) {
    Map<String, Object> kpiData = new HashMap<>();

    try {
      requireLocation(locationId);
      int agingChequesCount = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM cheque_reader WHERE office_code = ? AND date_created::date <= CURRENT_DATE - INTERVAL '30 days' " +
          "AND (date_deleted IS NULL OR TRIM(date_deleted) = '')",
        Integer.class,
        locationId
      );

      String dailyScansSql =
        "SELECT COUNT(*) FROM cheque_reader WHERE office_code = ? AND date_created::date = CURRENT_DATE AND (date_deleted IS NULL OR TRIM(date_deleted) = '')";
      Long dailyScansCount = jdbcTemplate.queryForObject(
        dailyScansSql,
        Long.class,
        locationId
      );

      String outstandingSql =
        "SELECT SUM(value_doc) FROM source_system_records WHERE office_code = ? AND (indicator IS NULL OR indicator = 0)";
      BigDecimal totalOutstanding = jdbcTemplate.queryForObject(
        outstandingSql,
        BigDecimal.class,
        locationId
      );

      kpiData.put("dailyScans", dailyScansCount != null ? dailyScansCount : 0);
      kpiData.put(
        "totalOutstanding",
        totalOutstanding != null ? totalOutstanding : BigDecimal.ZERO
      );
      kpiData.put("agingCheques", agingChequesCount);
      kpiData.put("success", true);
    } catch (Exception e) {
      kpiData.put("success", false);
      kpiData.put(
        "message",
        "Error fetching dashboard KPIs: " + e.getMessage()
      );

      kpiData.put("dailyScans", 0);
      kpiData.put("totalOutstanding", BigDecimal.ZERO);
      kpiData.put("agingCheques", 0);
    }

    return kpiData;
  }

  public List<Map<String, Object>> getRecentReceipts(String locationId) {
    List<Map<String, Object>> allReceipts =
      receiptService.getAllReceiptsWithActions(locationId);

    //        if (allReceipts != null && allReceipts.size() > 5) {
    //            return allReceipts.subList(0, 5);
    //        }
    return allReceipts;
  }

  public Map<String, Object> getReceiptSummary(String locationId) {
    Map<String, Object> response = new HashMap<>();

    try {
      requireLocation(locationId);
      String totalReceiptsSql =
        "SELECT COUNT(*) FROM Receipt WHERE office_code = ?";
      Long totalReceipts = jdbcTemplate.queryForObject(
        totalReceiptsSql,
        Long.class,
        locationId
      );

      String undoCountSql =
        "SELECT COUNT(*) FROM Receipt WHERE office_code = ? AND status = '1'";
      Long undoCount = jdbcTemplate.queryForObject(
        undoCountSql,
        Long.class,
        locationId
      );

      String removedInvoiceSql =
        "SELECT COUNT(*) FROM source_system_records WHERE office_code = ? AND indicator = -1";
      Long removedInvoiceCount = jdbcTemplate.queryForObject(
        removedInvoiceSql,
        Long.class,
        locationId
      );

      String postedToCodaSql =
        "SELECT COUNT(*) FROM Receipt WHERE office_code = ? AND posted_to_coda = '1'";
      Long postedToCodaCount = jdbcTemplate.queryForObject(
        postedToCodaSql,
        Long.class,
        locationId
      );

      response.put("totalReceipts", totalReceipts != null ? totalReceipts : 0);
      response.put("undoCount", undoCount != null ? undoCount : 0);
      response.put(
        "removedInvoiceCount",
        removedInvoiceCount != null ? removedInvoiceCount : 0
      );
      response.put(
        "postedToCodaCount",
        postedToCodaCount != null ? postedToCodaCount : 0
      );
      response.put("success", true);
    } catch (Exception e) {
      response.put("success", false);
      response.put(
        "message",
        "Error fetching receipt summary: " + e.getMessage()
      );
    }

    return response;
  }

  private void requireLocation(String locationId) {
    if (
      locationId == null || locationId.isBlank()
    ) throw new IllegalArgumentException("locationId is required.");
  }
}
