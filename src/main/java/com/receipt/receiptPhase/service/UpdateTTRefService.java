package com.receipt.receiptPhase.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UpdateTTRefService {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public Map<String, Object> findByTTNo(String ttNo) {
        String sql = "SELECT r.Transaction_No, i.customer_name, r.Reference_No, r.Currency, r.Amount, r.Paid_Invoice_Total " +
                "FROM Receipt r " +
                "INNER JOIN Invoice i ON i.Transaction_No = r.Transaction_No " +
                "WHERE r.Reference_No = ? AND COALESCE(r.Posted_To_CODA, B'0') = B'0' AND COALESCE(r.Status, B'0') = B'0'";
        try {
            return jdbcTemplate.queryForMap(sql, ttNo);
        } catch (Exception e) {
            return null;
        }
    }


    @Transactional
    public void updateTTNo(String oldNo, String newNo, String transNo, String remark, String userId) {
        // Update Receipt
        String updateSql = "UPDATE RECEIPT SET REFERENCE_NO = ? WHERE REFERENCE_NO = ? AND TRANSACTION_NO = ? " +
                "AND COALESCE(POSTED_TO_CODA, B'0') = B'0' AND COALESCE(Status, B'0') = B'0'";
        jdbcTemplate.update(updateSql, newNo, oldNo, transNo);

        // Audit Log
        String auditSql = "INSERT INTO RECEIPT_AUDITLOG(ORIGINAL_CHEQUENO, TRANSACTION_NO, NEW_CHEQUENO, USER_ID, ACTION_DATE, REASON, payment_mode) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'T/T')";
        jdbcTemplate.update(auditSql, oldNo, transNo, newNo, userId, LocalDateTime.now(), remark);
    }
}