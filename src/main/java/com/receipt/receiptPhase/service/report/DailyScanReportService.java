package com.receipt.receiptPhase.service.report;

import com.receipt.receiptPhase.dto.report.DailyScanReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@Service
public class DailyScanReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<DailyScanReport> getDailyScanReport(LocalDate fromDate, LocalDate toDate, String bound) {

        String fromStr = fromDate.toString();
        String toStr = toDate.toString();

        String boundCondition = "";
        if ("I".equalsIgnoreCase(bound.trim())) {
            boundCondition = "AND TRIM(bound) = 'I' ";
        } else if ("O".equalsIgnoreCase(bound.trim())) {
            boundCondition = "AND TRIM(bound) = 'O' ";
        } else if ("IO".equalsIgnoreCase(bound.trim())) {
            boundCondition = "AND TRIM(bound) = 'IO' ";
        } else {
            boundCondition = "AND TRIM(bound) IN ('I', 'O', 'IO') ";
        }

        String sql = "SELECT " +
                "   CASE WHEN TRIM(bound) = 'I' THEN 'INBOUND' WHEN TRIM(bound) = 'O' THEN 'OUTBOUND' ELSE 'INBOUND AND OUTBOUND' END AS bound, " +
                "   full_cheque_no, cheque_no, bank_name, scan_user_id, " +
                "   date_created AS create_time, " +
                "   CASE WHEN auto_read = '1' OR auto_read = 'Y' OR auto_read = 'T' THEN 'TRUE' ELSE 'FALSE' END AS auto_read " +
                "FROM cheque_reader " +
                "WHERE SUBSTRING(TRIM(date_created), 1, 10) >= ? " +
                "  AND SUBSTRING(TRIM(date_created), 1, 10) <= ? " +
                boundCondition +
                "  AND (date_deleted IS NULL OR TRIM(date_deleted) = '') " +
                "ORDER BY date_created DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            DailyScanReport dto = new DailyScanReport();
            dto.setBound(rs.getString("bound"));
            dto.setFullChequeNo(rs.getString("full_cheque_no"));
            dto.setChequeNo(rs.getString("cheque_no"));
            dto.setBankName(rs.getString("bank_name"));
            dto.setScanUserId(rs.getString("scan_user_id"));
            dto.setCreateTime(rs.getString("create_time") != null ? rs.getString("create_time") : "");
            dto.setAutoRead(rs.getString("auto_read"));
            return dto;
        }, fromStr, toStr);
    }

    public ByteArrayInputStream generateCsvReport(LocalDate fromDate, LocalDate toDate, String bound) {
        List<DailyScanReport> records = getDailyScanReport(fromDate, toDate, bound);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        writer.println("BOUND,FULL CHEQUE NO,CHEQUE NO,BANK NAME,USER ID,CREATE TIME,AUTO READ");

        for (DailyScanReport item : records) {
            String bnd = cleanCsvField(item.getBound());
            String fullNo = cleanCsvField(item.getFullChequeNo());
            String chqNo = cleanCsvField(item.getChequeNo());
            String bank = cleanCsvField(item.getBankName());
            String userId = cleanCsvField(item.getScanUserId());
            String createTime = cleanCsvField(item.getCreateTime());
            String autoRead = cleanCsvField(item.getAutoRead());

            writer.println(String.format("%s,%s,%s,%s,%s,%s,%s", bnd, fullNo, chqNo, bank, userId, createTime, autoRead));
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