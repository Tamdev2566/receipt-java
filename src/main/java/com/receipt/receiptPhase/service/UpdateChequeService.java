package com.receipt.receiptPhase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        String maxIdSql = "SELECT COALESCE(MAX(CAST(NULLIF(log_id, '') AS INTEGER)), 0) + 1 FROM RECEIPT_AUDITLOG WHERE log_id <> ''";
        Integer nextId = jdbcTemplate.queryForObject(maxIdSql, Integer.class);

        String auditSql = "INSERT INTO RECEIPT_AUDITLOG (log_id, original_cheque_no, transaction_no, new_cheque_no, reason, payment_mode, action_date, action_created_user) " +
                "VALUES (?, ?, ?, ?, ?, 'Cheque', ?, ?)";

        String safeOldNo = (oldNo != null && oldNo.length() > 32) ? oldNo.substring(0, 32) : oldNo;
        String safeNewNo = (newNo != null && newNo.length() > 32) ? newNo.substring(0, 32) : newNo;
        String safeTransNo = (transNo != null && transNo.length() > 20) ? transNo.substring(0, 20) : transNo;
        String safeRemark = (remark != null && remark.length() > 200) ? remark.substring(0, 200) : remark;
        String safeUser = (userId != null && userId.length() > 30) ? userId.substring(0, 30) : userId;
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        jdbcTemplate.update(auditSql, nextId, safeOldNo, safeTransNo, safeNewNo, safeRemark, dateStr, safeUser);
    }
}