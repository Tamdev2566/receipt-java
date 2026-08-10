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

@Service
public class RemoveInvoiceService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<Map<String, Object>> getInvoices(String customer, String vessel, String voyage) {
        String sql = "SELECT * FROM source_system_records WHERE COALESCE(indicator, 0) = 0 " +
                "AND customer_name = ? AND vessel_name = ? AND voyage_no = ?";
        return jdbcTemplate.queryForList(sql, customer, vessel, voyage);
    }

    public Map<String, Object> getDetailsByTransaction(String transactionNo) {

        String sql = "SELECT s.customer_name, s.vessel_name, s.voyage_no " +
                "FROM source_system_records s " +
                "INNER JOIN invoice i ON s.reference_no = i.reference_no " +
                "WHERE i.transaction_no = ?";

        try {
            return jdbcTemplate.queryForMap(sql, transactionNo);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Transactional
    public void removeInvoices(List<String> referenceNos, String userId, String remark) {
        String actionDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        for (String refNo : referenceNos) {
            jdbcTemplate.update("UPDATE source_system_records SET indicator = -1 WHERE reference_no = ? AND COALESCE(indicator, 0) = 0", refNo);


            jdbcTemplate.update("INSERT INTO partial_Archive SELECT * FROM partial WHERE reference_no = ?", refNo);
            jdbcTemplate.update("DELETE FROM partial WHERE reference_no = ?", refNo);


            String auditSql = "INSERT INTO RECEIPT_AUDITLOG (REMOVED_INVOICE_NO, ACTION_CREATED_USER, ACTION_DATE, REASON) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(auditSql, refNo, userId, actionDate, remark);

        }
    }
}
