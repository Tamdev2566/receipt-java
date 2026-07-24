package com.receipt.receiptPhase.service.ediCoda;

import com.receipt.receiptPhase.dto.ediCoda.EdiToCoda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class EdiToCodaService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<EdiToCoda> retrieveReceipts(LocalDate fromDate, LocalDate toDate) {

        String fromStr = fromDate.toString();
        String toStr = toDate.toString();


        String sql = "SELECT r.reference_no, " +
                "       r.currency_code AS currency, " +
                "       r.amount, " +
                "       r.transaction_no, " +
                "       (SELECT customer_name FROM invoice WHERE transaction_no = r.transaction_no LIMIT 1) AS customer_name " +
                "FROM receipt r " +
                "WHERE (r.status IS NULL OR r.status = '0'::bit) " +
                "  AND (r.posted_to_coda IS NULL OR r.posted_to_coda = '0'::bit) " +
                "  AND SUBSTRING(TRIM(r.transaction_date), 1, 10) >= ? " +
                "  AND SUBSTRING(TRIM(r.transaction_date), 1, 10) <= ? " +
                "ORDER BY r.transaction_date";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            EdiToCoda dto = new EdiToCoda();
            dto.setReferenceNo(rs.getString("reference_no"));
            dto.setCurrency(rs.getString("currency") != null ? rs.getString("currency") : "");
            dto.setAmount(rs.getBigDecimal("amount"));
            dto.setCustomerName(rs.getString("customer_name") != null ? rs.getString("customer_name") : "");
            return dto;
        }, fromStr, toStr);
    }


    @Transactional
    public String exportEdiReceipts(LocalDate fromDate, LocalDate toDate) {

        String fromStr = fromDate.toString();
        String toStr = toDate.toString();

        String fetchSql = "SELECT transaction_no, receipt_date, currency_code AS currency FROM receipt " +
                "WHERE (status IS NULL OR status = '0'::bit) " +
                "  AND (posted_to_coda IS NULL OR posted_to_coda = '0'::bit) " +
                "  AND SUBSTRING(TRIM(transaction_date), 1, 10) >= ? " +
                "  AND SUBSTRING(TRIM(transaction_date), 1, 10) <= ? " +
                "ORDER BY transaction_no";

        List<Map<String, Object>> receipts = jdbcTemplate.queryForList(fetchSql, fromStr, toStr);

        if (receipts.isEmpty()) {
            return "No receipts found to export for the given date range.";
        }

        String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        String updateSql = "UPDATE receipt " +
                "SET posted_to_coda = '1'::bit, coda_post_date = ? " +
                "WHERE (status IS NULL OR status = '0'::bit) " +
                "  AND (posted_to_coda IS NULL OR posted_to_coda = '0'::bit) " +
                "  AND SUBSTRING(TRIM(transaction_date), 1, 10) >= ? " +
                "  AND SUBSTRING(TRIM(transaction_date), 1, 10) <= ?";

        int updatedRows = jdbcTemplate.update(updateSql, currentTimestamp, fromStr, toStr);

        return "Successfully exported " + updatedRows + " receipt record(s) to CODA.";
    }
}