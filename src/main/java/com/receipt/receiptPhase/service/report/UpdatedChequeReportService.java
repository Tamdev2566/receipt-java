package com.receipt.receiptPhase.service.report;


import com.receipt.receiptPhase.dto.report.UpdatedChequeReport;
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
public class UpdatedChequeReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<UpdatedChequeReport> getUpdatedChequeReport(LocalDate fromDate, LocalDate toDate) {

        String startStr = fromDate.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endStr = toDate.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String sql = "SELECT DISTINCT transaction_no, original_cheque_no, new_cheque_no, action_created_user, action_date, reason " +
                "FROM receipt_auditlog " +
                "WHERE payment_mode = 'Cheque' " +
                "  AND original_cheque_no IS NOT NULL " +
                "  AND new_cheque_no IS NOT NULL " +
                "  AND action_date >= ? AND action_date <= ? " +
                "ORDER BY action_date DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            UpdatedChequeReport dto = new UpdatedChequeReport();
            dto.setTransactionNo(rs.getString("transaction_no"));
            dto.setOriginalChequeNo(rs.getString("original_cheque_no"));
            dto.setNewChequeNo(rs.getString("new_cheque_no"));
            dto.setUserId(rs.getString("action_created_user"));
            dto.setActionDate(rs.getString("action_date") != null ? rs.getString("action_date") : "");
            dto.setReason(rs.getString("reason"));
            return dto;
        }, startStr, endStr);
    }


    public ByteArrayInputStream generateCsvReport(LocalDate fromDate, LocalDate toDate) {
        List<UpdatedChequeReport> records = getUpdatedChequeReport(fromDate, toDate);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);


        writer.println("TRANSACTION NO,ORIGINAL CHEQUE NO,NEW CHEQUE NO,UPDATED BY USER,UPDATED DATE,REASON");

        for (UpdatedChequeReport item : records) {
            String txnNo = cleanCsvField(item.getTransactionNo());
            String origNo = cleanCsvField(item.getOriginalChequeNo());
            String newNo = cleanCsvField(item.getNewChequeNo());
            String userId = cleanCsvField(item.getUserId());
            String dateStr = cleanCsvField(item.getActionDate());
            String reason = cleanCsvField(item.getReason());

            writer.println(String.format("%s,%s,%s,%s,%s,%s", txnNo, origNo, newNo, userId, dateStr, reason));
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