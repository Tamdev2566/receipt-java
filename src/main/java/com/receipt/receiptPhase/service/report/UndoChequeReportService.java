package com.receipt.receiptPhase.service.report;


import com.receipt.receiptPhase.dto.report.UndoChequeReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UndoChequeReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<UndoChequeReport> getUndoChequeReport(LocalDate fromDate, LocalDate toDate) {

        String startStr = fromDate.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endStr = toDate.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        String sql = "SELECT DISTINCT cancelled_cheque_no, full_cheque_no, reason, action_created_user, action_date " +
                "FROM receipt_auditlog " +
                "WHERE cancelled_cheque_no IS NOT NULL " +
                "  AND action_date >= ? AND action_date <= ? " +
                "ORDER BY action_date DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            UndoChequeReport dto = new UndoChequeReport();
            dto.setCancelledChequeNo(rs.getString("cancelled_cheque_no"));
            dto.setFullChequeNo(rs.getString("full_cheque_no"));
            dto.setReason(rs.getString("reason"));
            dto.setUserId(rs.getString("action_created_user"));


            String dateStr = rs.getString("action_date");
            dto.setActionDateString(dateStr != null ? dateStr : "");
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                try {

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    dto.setActionDate(LocalDateTime.parse(dateStr.trim(), formatter));
                } catch (Exception e) {
                    dto.setActionDate(null);
                }
            }

            return dto;
        }, startStr, endStr);
    }
    public ByteArrayInputStream generateCsvReport(LocalDate fromDate, LocalDate toDate) {
        List<UndoChequeReport> records = getUndoChequeReport(fromDate, toDate);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);


        writer.println("UNDO CHEQUE NO,FULL CHEQUE NO,REASON,UNDO BY USER,UNDO DATE");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (UndoChequeReport item : records) {
            String cancelledNo = cleanCsvField(item.getCancelledChequeNo());
            String fullNo = cleanCsvField(item.getFullChequeNo());
            String reason = cleanCsvField(item.getReason());
            String userId = cleanCsvField(item.getUserId());
            String dateStr = item.getActionDate() != null ? item.getActionDate().format(formatter) : "";

            writer.println(String.format("%s,%s,%s,%s,%s", cancelledNo, fullNo, reason, userId, dateStr));
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