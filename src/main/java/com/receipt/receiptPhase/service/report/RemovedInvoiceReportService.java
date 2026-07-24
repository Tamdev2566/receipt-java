package com.receipt.receiptPhase.service.report;

import com.receipt.receiptPhase.dto.report.RemovedInvoiceReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RemovedInvoiceReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<RemovedInvoiceReport> getRemovedInvoiceReport(LocalDate fromDate, LocalDate toDate) {

        String startStr = fromDate.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endStr = toDate.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Column names mapped to PostgreSQL receipt_auditlog table
        String sql = "SELECT DISTINCT removed_invoice_no, invoice_source, action_created_user, action_date, reason " +
                "FROM receipt_auditlog " +
                "WHERE removed_invoice_no IS NOT NULL " +
                "  AND action_date >= ? AND action_date <= ? " +
                "ORDER BY action_date DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RemovedInvoiceReport dto = new RemovedInvoiceReport();
            dto.setRemovedInvoice(rs.getString("removed_invoice_no"));
            dto.setInvoiceSource(rs.getString("invoice_source"));
            dto.setUserId(rs.getString("action_created_user"));
            dto.setActionDate(rs.getString("action_date") != null ? rs.getString("action_date") : "");
            dto.setReason(rs.getString("reason"));
            return dto;
        }, startStr, endStr);
    }


    public ByteArrayInputStream generateCsvReport(LocalDate fromDate, LocalDate toDate) {
        List<RemovedInvoiceReport> records = getRemovedInvoiceReport(fromDate, toDate);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);


        writer.println("REMOVED INVOICE,SOURCE,REMOVED BY USER,REMOVED DATE,REASON");

        for (RemovedInvoiceReport item : records) {
            String invNo = cleanCsvField(item.getRemovedInvoice());
            String source = cleanCsvField(item.getInvoiceSource());
            String userId = cleanCsvField(item.getUserId());
            String dateStr = cleanCsvField(item.getActionDate());
            String reason = cleanCsvField(item.getReason());

            writer.println(String.format("%s,%s,%s,%s,%s", invNo, source, userId, dateStr, reason));
        }

        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String cleanCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            field = "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}