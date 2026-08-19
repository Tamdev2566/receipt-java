package com.receipt.receiptPhase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.springframework.dao.EmptyResultDataAccessException;

@Service
public class UndoChequeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Map<String, Object> getChequeDetails(String chequeNo, String fullChequeNo, String locationId) {
        String sql = "SELECT bound, bank_name, scan_user_id FROM cheque_reader " +
                "WHERE TO_TIMESTAMP(date_created, 'YYYY-MM-DD HH24:MI:SS') >= CURRENT_TIMESTAMP - INTERVAL '5 years' " +
                "AND (date_deleted IS NULL OR TRIM(date_deleted) = '') " +
                "AND office_code = ? AND TRIM(cheque_no) = TRIM(?) AND TRIM(full_cheque_no) = TRIM(?)";
        try {
            return jdbcTemplate.queryForMap(sql, requiredLocation(locationId), chequeNo, fullChequeNo);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyMap();
        }
    }

    @Transactional

    public void undoCheque(String chequeNo, String fullChequeNo, String remark, String userId, String locationId) {
        String formattedDate = LocalDateTime.now().format(FORMATTER);

        String truncatedFullChequeNo = (fullChequeNo != null && fullChequeNo.length() > 32)
                ? fullChequeNo.substring(0, 32)
                : fullChequeNo;

        String updateSql = "UPDATE cheque_reader SET date_deleted = ?, user_deleted = ? WHERE cheque_no = ? AND full_cheque_no = ? AND office_code = ?";
        jdbcTemplate.update(updateSql, formattedDate, userId, chequeNo, fullChequeNo, requiredLocation(locationId));

        String logId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        String auditSql = "INSERT INTO receipt_auditlog (log_id, cancelled_cheque_no, reason, action_created_user, action_date, full_cheque_no, office_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(auditSql, logId, chequeNo, remark, userId, formattedDate, truncatedFullChequeNo, requiredLocation(locationId));
    }

    private String requiredLocation(String locationId) {
        if (locationId == null || locationId.isBlank()) throw new IllegalArgumentException("locationId is required.");
        return locationId.trim();
    }
}
