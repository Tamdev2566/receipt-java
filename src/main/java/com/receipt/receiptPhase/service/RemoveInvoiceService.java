package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.SourceSystemRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RemoveInvoiceService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getInvoices(String customer, String vessel, String voyage, String locationId) {
        String sql = "SELECT * FROM source_system_records WHERE COALESCE(indicator, 0) = 0 " +
                "AND office_code = ? AND customer_name = ? AND vessel_name = ? AND voyage_no = ?";
        return jdbcTemplate.queryForList(sql, requiredLocation(locationId), customer, vessel, voyage);
    }

    public Map<String, Object> getDetailsByTransaction(String transactionNo, String locationId) {

        String sql = "SELECT s.customer_name, s.vessel_name, s.voyage_no " +
                "FROM source_system_records s " +
                "INNER JOIN invoice i ON s.reference_no = i.reference_no " +
                "INNER JOIN receipt r ON r.transaction_no = i.transaction_no " +
                "WHERE i.transaction_no = ? AND r.office_code = ?";

        try {
            return jdbcTemplate.queryForMap(sql, transactionNo, requiredLocation(locationId));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional
    public void removeInvoices(List<String> referenceNos, String userId, String remark, String locationId) {
        locationId = requiredLocation(locationId);
        String actionDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        for (String refNo : referenceNos) {
            jdbcTemplate.update("UPDATE source_system_records SET indicator = -1 WHERE reference_no = ? AND office_code = ? AND COALESCE(indicator, 0) = 0", refNo, locationId);

            jdbcTemplate.update("INSERT INTO partial_Archive SELECT * FROM partial WHERE reference_no = ? AND office_code = ?", refNo, locationId);
            jdbcTemplate.update("DELETE FROM partial WHERE reference_no = ? AND office_code = ?", refNo, locationId);

            String logId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

            String auditSql = "INSERT INTO RECEIPT_AUDITLOG (log_id, REMOVED_INVOICE_NO, ACTION_CREATED_USER, ACTION_DATE, REASON, office_code) VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(auditSql, logId, refNo, userId, actionDate, remark, locationId);
        }
    }

    private String requiredLocation(String locationId) {
        if (locationId == null || locationId.isBlank()) throw new IllegalArgumentException("locationId is required.");
        return locationId.trim();
    }
}
