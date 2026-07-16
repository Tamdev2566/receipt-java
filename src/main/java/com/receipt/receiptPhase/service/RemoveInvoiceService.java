package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.RemoveInvoiceRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RemoveInvoiceService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RemoveInvoiceService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeInvoices(RemoveInvoiceRequest request) {

        if (request.getInvoices() == null || request.getInvoices().isEmpty()) {
            throw new IllegalArgumentException("Please select invoice no(s) to remove.");
        }

        String actionDate = LocalDateTime.now().format(formatter);
        String userId = request.getUserId() != null ? request.getUserId().trim() : "System";
        String reason = request.getReason() != null ? request.getReason().trim() : "";

        for (RemoveInvoiceRequest.InvoiceItem item : request.getInvoices()) {

            String refNo = item.getReferenceNo();
            String source = item.getSource();

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("referenceNo", refNo);


            String updateSourceSys = "UPDATE source_system_records SET indicator = -1 " +
                    "WHERE reference_no = :referenceNo AND (indicator IS NULL OR indicator = 0)";
            jdbcTemplate.update(updateSourceSys, params);


            String archivePartial = "INSERT INTO partial_archive SELECT * FROM partial WHERE reference_no = :referenceNo";
            jdbcTemplate.update(archivePartial, params);

            String deletePartial = "DELETE FROM partial WHERE reference_no = :referenceNo";
            jdbcTemplate.update(deletePartial, params);


            String insertAuditLog = "INSERT INTO receipt_auditlog (removed_invoice, invoicesource, user_id, action_date, reason) " +
                    "SELECT :removedInvoice, :invoiceSource, :userId, :actionDate, :reason " +
                    "WHERE NOT EXISTS (SELECT 1 FROM receipt_auditlog WHERE removed_invoice = :removedInvoice)";

            MapSqlParameterSource auditParams = new MapSqlParameterSource();
            auditParams.addValue("removedInvoice", refNo);
            auditParams.addValue("invoiceSource", source);
            auditParams.addValue("userId", userId);
            auditParams.addValue("actionDate", actionDate); // PostgreSQL timestamp format
            auditParams.addValue("reason", reason);

            jdbcTemplate.update(insertAuditLog, auditParams);
        }
    }
}