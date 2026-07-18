package com.receipt.receiptPhase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;

@Service
public class UndoChequeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getChequeDetails(String chequeNo, String fullChequeNo) {
        String sql = "SELECT BOUND, BANK_NAME, SCAN_USER_ID FROM CHEQUE_READER " +
                "WHERE DATEDIFF(YEAR, CREATE_TIME, GETDATE()) <= 5 " +
                "AND ISNULL(DELETED, 0) = 0 AND CHEQUE_NO = ? AND FULL_CHEQUE_NO = ?";
        try {
            return jdbcTemplate.queryForMap(sql, chequeNo, fullChequeNo);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyMap();
        }
    }


    @Transactional
    public void undoCheque(String chequeNo, String fullChequeNo, String remark, String userId) {

        String updateSql = "UPDATE CHEQUE_READER SET DELETED = 1 WHERE CHEQUE_NO = ? AND FULL_CHEQUE_NO = ?";
        jdbcTemplate.update(updateSql, chequeNo, fullChequeNo);

        String auditSql = "INSERT INTO Receipt_AuditLog (Cancelled_ChequeNo, Reason, User_ID, Action_Date, Full_Cheque_No) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(auditSql, chequeNo, remark, userId, LocalDateTime.now(), fullChequeNo);
    }
}