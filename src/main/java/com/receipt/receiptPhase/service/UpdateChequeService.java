package com.receipt.receiptPhase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UpdateChequeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> findByChequeNo(String chequeNo) {

        String sql = "SELECT r.transaction_No, i.customer_name, r.reference_No, " +
                "r.currency_code, r.amount, r.paid_invoice_total " +
                "FROM Receipt r " +
                "INNER JOIN Invoice i ON i.transaction_No = r.transaction_No " +
                "WHERE r.reference_No = ? " +
                "AND COALESCE(r.posted_to_coda, B'0') = B'0' " +
                "AND COALESCE(r.status, B'0') = B'0'";
        try {
            return jdbcTemplate.queryForMap(sql, chequeNo);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Transactional
    public void updateChequeNo(String oldNo, String newNo, String transNo, String remark, String userId) {

        String sql = "UPDATE RECEIPT SET REFERENCE_NO = ? WHERE REFERENCE_NO = ? AND TRANSACTION_NO = ? " +
                "AND COALESCE(POSTED_TO_CODA, B'0') = B'0' AND COALESCE(Status, B'0') = B'0'";
        jdbcTemplate.update(sql, newNo, oldNo, transNo);

        String auditSql = "INSERT INTO RECEIPT_AUDITLOG (original_cheque_no, transaction_no, new_cheque_no, reason, payment_mode, action_date, action_created_user) " +
                "VALUES (?, ?, ?, ?, 'Cheque', ?, ?)";
        jdbcTemplate.update(auditSql, oldNo, transNo, newNo, remark, LocalDateTime.now(), userId);
    }
}