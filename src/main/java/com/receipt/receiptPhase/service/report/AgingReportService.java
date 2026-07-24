package com.receipt.receiptPhase.service.report;

import com.receipt.receiptPhase.dto.report.AgingReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Service
public class AgingReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<AgingReport> getAgingReport(int days) {


        String sql = "SELECT " +
                "   CASE WHEN bound = 'I' THEN 'INBOUND' WHEN bound = 'O' THEN 'OUTBOUND' ELSE 'INBOUND AND OUTBOUND' END AS bound, " +
                "   full_cheque_no, cheque_no, bank_name, scan_user_id, " +
                "   date_created AS create_time, " +
                "   (CURRENT_DATE - date_created::date) AS aging, " +
                "   CASE WHEN auto_read = '1' OR auto_read = 'Y' OR auto_read = 'T' THEN 'TRUE' ELSE 'FALSE' END AS auto_read " +
                "FROM cheque_reader " +
                "WHERE (CURRENT_DATE - date_created::date) >= ? " +
                "  AND (CURRENT_DATE - date_created::date) <= (5 * 365) " +
                "  AND date_deleted IS NULL " +
                "  AND cheque_no NOT IN ( " +
                "      SELECT DISTINCT reference_no FROM receipt " +
                "      WHERE reference_no IN (SELECT cheque_no FROM cheque_reader) " +
                "        AND (status IS NULL OR status = '0'::bit) " +
                "  ) " +
                "ORDER BY date_created DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AgingReport dto = new AgingReport();
            dto.setBound(rs.getString("bound"));
            dto.setFullChequeNo(rs.getString("full_cheque_no"));
            dto.setChequeNo(rs.getString("cheque_no"));
            dto.setBankName(rs.getString("bank_name"));
            dto.setScanUserId(rs.getString("scan_user_id"));
            dto.setCreateTime(rs.getString("create_time") != null ? rs.getString("create_time") : "");
            dto.setAging(rs.getInt("aging"));
            dto.setAutoRead(rs.getString("auto_read"));
            return dto;
        }, days);
    }

    public ByteArrayInputStream generateCsvReport(int days) {
        List<AgingReport> records = getAgingReport(days);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        writer.println("BOUND,FULL CHEQUE NO,CHEQUE NO,BANK NAME,USER ID,CREATE TIME,AGING,AUTO READ");

        for (AgingReport item : records) {
            String bound = cleanCsvField(item.getBound());
            String fullNo = cleanCsvField(item.getFullChequeNo());
            String chqNo = cleanCsvField(item.getChequeNo());
            String bank = cleanCsvField(item.getBankName());
            String userId = cleanCsvField(item.getScanUserId());
            String createTime = cleanCsvField(item.getCreateTime());
            String aging = String.valueOf(item.getAging());
            String autoRead = cleanCsvField(item.getAutoRead());

            writer.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s", bound, fullNo, chqNo, bank, userId, createTime, aging, autoRead));
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