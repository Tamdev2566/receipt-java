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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UndoChequeReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<UndoChequeReport> getUndoChequeReport(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.atTime(LocalTime.MAX);

        String sql = "SELECT DISTINCT CANCELLED_CHEQUENO, FULL_CHEQUE_NO, REASON, USER_ID, ACTION_DATE " +
                "FROM RECEIPT_AUDITLOG " +
                "WHERE CANCELLED_CHEQUENO IS NOT NULL " +
                "  AND ACTION_DATE >= ? AND ACTION_DATE <= ? " +
                "ORDER BY ACTION_DATE DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new UndoChequeReport(
                rs.getString("CANCELLED_CHEQUENO"),
                rs.getString("FULL_CHEQUE_NO"),
                rs.getString("REASON"),
                rs.getString("USER_ID"),
                rs.getTimestamp("ACTION_DATE") != null ? rs.getTimestamp("ACTION_DATE").toLocalDateTime() : null
        ), startDateTime, endDateTime);
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