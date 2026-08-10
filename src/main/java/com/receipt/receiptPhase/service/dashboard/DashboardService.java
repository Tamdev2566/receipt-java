package com.receipt.receiptPhase.service.dashboard;

import com.receipt.receiptPhase.service.ReceiptService;
import com.receipt.receiptPhase.service.report.AgingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private AgingReportService agingReportService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getDashboardKPIs() {
        Map<String, Object> kpiData = new HashMap<>();

        try {

            int agingChequesCount = agingReportService.getAgingReport(30).size();

            String dailyScansSql = "SELECT COUNT(*) FROM cheque_reader WHERE date_created::date = CURRENT_DATE AND (date_deleted IS NULL OR TRIM(date_deleted) = '')";
            Long dailyScansCount = jdbcTemplate.queryForObject(dailyScansSql, Long.class);


            String outstandingSql = "SELECT SUM(value_doc) FROM source_system_records WHERE indicator IS NULL OR indicator = 0";
            BigDecimal totalOutstanding = jdbcTemplate.queryForObject(outstandingSql, BigDecimal.class);


            kpiData.put("dailyScans", dailyScansCount != null ? dailyScansCount : 0);
            kpiData.put("totalOutstanding", totalOutstanding != null ? totalOutstanding : BigDecimal.ZERO);
            kpiData.put("agingCheques", agingChequesCount);
            kpiData.put("success", true);

        } catch (Exception e) {
            kpiData.put("success", false);
            kpiData.put("message", "Error fetching dashboard KPIs: " + e.getMessage());

            kpiData.put("dailyScans", 0);
            kpiData.put("totalOutstanding", BigDecimal.ZERO);
            kpiData.put("agingCheques", 0);
        }

        return kpiData;
    }

    public List<Map<String, Object>> getRecentReceipts() {
        List<Map<String, Object>> allReceipts = receiptService.getAllReceiptsWithActions();

        if (allReceipts != null && allReceipts.size() > 5) {
            return allReceipts.subList(0, 5);
        }
        return allReceipts;
    }

    public Map<String, Object> getReceiptSummary() {
        Map<String, Object> response = new HashMap<>();

        try {

            String totalReceiptsSql = "SELECT COUNT(*) FROM Receipt";
            Long totalReceipts = jdbcTemplate.queryForObject(totalReceiptsSql, Long.class);


            String undoCountSql = "SELECT COUNT(*) FROM Receipt WHERE status = '1'";
            Long undoCount = jdbcTemplate.queryForObject(undoCountSql, Long.class);


            String removedInvoiceSql = "SELECT COUNT(*) FROM source_system_records WHERE indicator = -1";
            Long removedInvoiceCount = jdbcTemplate.queryForObject(removedInvoiceSql, Long.class);

            response.put("totalReceipts", totalReceipts != null ? totalReceipts : 0);
            response.put("undoCount", undoCount != null ? undoCount : 0);
            response.put("removedInvoiceCount", removedInvoiceCount != null ? removedInvoiceCount : 0);
            response.put("success", true);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching receipt summary: " + e.getMessage());
        }

        return response;
    }
}