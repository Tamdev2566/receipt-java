package com.receipt.receiptPhase.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveInvoiceService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<Map<String, Object>> getInvoices(
    String customer,
    String vessel,
    String voyage,
    String locationId
  ) {
    StringBuilder sql = new StringBuilder(
      "SELECT * FROM source_system_records WHERE COALESCE(indicator, 0) = 0 AND office_code = ?"
    );

    List<Object> params = new ArrayList<>();
    params.add(requiredLocation(locationId));

    if (customer != null && !customer.trim().isEmpty()) {
      sql.append(" AND customer_name = ?");
      params.add(customer.trim());
    }

    if (vessel != null && !vessel.trim().isEmpty()) {
      sql.append(" AND vessel_name = ?");
      params.add(vessel.trim());
    }

    if (voyage != null && !voyage.trim().isEmpty()) {
      sql.append(" AND voyage_no = ?");
      params.add(voyage.trim());
    }

    return jdbcTemplate.queryForList(sql.toString(), params.toArray());
  }

  public Map<String, Object> getDetailsByTransaction(
    String transactionNo,
    String locationId
  ) {
    String sql =
      "SELECT s.customer_name, s.vessel_name, s.voyage_no " +
      "FROM source_system_records s " +
      "INNER JOIN invoice i ON s.reference_no = i.reference_no " +
      "INNER JOIN receipt r ON r.transaction_no = i.transaction_no " +
      "WHERE i.transaction_no = ? AND r.office_code = ?";

    try {
      return jdbcTemplate.queryForMap(
        sql,
        transactionNo,
        requiredLocation(locationId)
      );
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Transactional
  public void removeInvoices(
    List<String> referenceNos,
    String userId,
    String remark,
    String locationId
  ) {
    locationId = requiredLocation(locationId);
    String actionDate = LocalDateTime.now().format(
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    );

    for (String refNo : referenceNos) {
      jdbcTemplate.update(
        "UPDATE source_system_records SET indicator = -1 WHERE reference_no = ? AND office_code = ? AND COALESCE(indicator, 0) = 0",
        refNo,
        locationId
      );

      jdbcTemplate.update(
        "INSERT INTO partial_Archive SELECT * FROM partial WHERE reference_no = ? AND office_code = ?",
        refNo,
        locationId
      );
      jdbcTemplate.update(
        "DELETE FROM partial WHERE reference_no = ? AND office_code = ?",
        refNo,
        locationId
      );

      String logId = UUID.randomUUID()
        .toString()
        .replace("-", "")
        .substring(0, 10);

      String auditSql =
        "INSERT INTO RECEIPT_AUDITLOG (log_id, REMOVED_INVOICE_NO, ACTION_CREATED_USER, ACTION_DATE, REASON, office_code) VALUES (?, ?, ?, ?, ?, ?)";
      jdbcTemplate.update(
        auditSql,
        logId,
        refNo,
        userId,
        actionDate,
        remark,
        locationId
      );
    }
  }

  private String requiredLocation(String locationId) {
    if (
      locationId == null || locationId.isBlank()
    ) throw new IllegalArgumentException("locationId is required.");
    return locationId.trim();
  }
}
