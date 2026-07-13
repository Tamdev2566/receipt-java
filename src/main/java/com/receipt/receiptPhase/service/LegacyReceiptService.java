package com.receipt.receiptPhase.service;

import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LegacyReceiptService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LegacyReceiptService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getBanks() {
        return jdbcTemplate.queryForList("""
                SELECT *
                FROM master_banks
                ORDER BY bank_name
                """, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getAccounts(String currencyCode, String paymentMode) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currencyCode", currencyCode)
                .addValue("paymentMode", paymentMode);

        return jdbcTemplate.queryForList("""
                SELECT *
                FROM master_accounts
                WHERE (:currencyCode IS NULL OR currency_code = :currencyCode)
                  AND (:paymentMode IS NULL OR payment_mode = :paymentMode)
                ORDER BY account_code
                """, params);
    }

    public List<Map<String, Object>> getReceipts(String transactionNo, String referenceNo, String paymentMode,
                                                String fromDate, String toDate) {
        QueryParts query = new QueryParts("""
                SELECT *
                FROM receipt
                WHERE 1 = 1
                """);

        query.addEquals("transaction_no", "transactionNo", transactionNo);
        query.addEquals("reference_no", "referenceNo", referenceNo);
        query.addEquals("payment_mode", "paymentMode", paymentMode);
        query.addDateRange("transaction_date", "fromDate", fromDate, "toDate", toDate);
        query.sql.append(" ORDER BY modified_date DESC, transaction_no DESC");

        return jdbcTemplate.queryForList(query.sql.toString(), query.params);
    }

    public List<Map<String, Object>> getReceiptReferenceOptions(String paymentMode) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("paymentMode", paymentMode);
        return jdbcTemplate.queryForList("""
                SELECT DISTINCT reference_no
                FROM receipt
                WHERE status = B'1'
                  AND (posted_to_coda IS NULL OR posted_to_coda = B'0')
                  AND (:paymentMode IS NULL OR payment_mode = :paymentMode)
                  AND reference_no IS NOT NULL
                  AND reference_no <> ''
                ORDER BY reference_no
                """, params);
    }

    public int updateReceiptReference(Map<String, Object> request) {
        String transactionNo = requiredString(request, "transactionNo");
        String originalReferenceNo = requiredString(request, "originalReferenceNo");
        String newReferenceNo = requiredString(request, "newReferenceNo");
        String userId = optionalString(request, "userId");
        String reason = optionalString(request, "reason");
        String paymentMode = optionalString(request, "paymentMode");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("transactionNo", transactionNo)
                .addValue("originalReferenceNo", originalReferenceNo)
                .addValue("newReferenceNo", newReferenceNo)
                .addValue("userId", userId)
                .addValue("actionDate", now())
                .addValue("reason", reason)
                .addValue("paymentMode", paymentMode);

        int updated = jdbcTemplate.update("""
                UPDATE receipt
                SET reference_no = :newReferenceNo,
                    modified_date = :actionDate
                WHERE transaction_no = :transactionNo
                  AND reference_no = :originalReferenceNo
                  AND status = B'1'
                  AND (posted_to_coda IS NULL OR posted_to_coda = B'0')
                """, params);

        if (updated > 0) {
            jdbcTemplate.update("""
                    INSERT INTO receipt_auditlog (
                        original_chequeno,
                        transaction_no,
                        new_chequeno,
                        user_id,
                        action_date,
                        reason,
                        payment_mode
                    )
                    VALUES (
                        :originalReferenceNo,
                        :transactionNo,
                        :newReferenceNo,
                        :userId,
                        :actionDate,
                        :reason,
                        :paymentMode
                    )
                    """, params);
        }

        return updated;
    }

    public List<Map<String, Object>> getChequeReader(String chequeNo, String fullChequeNo) {
        QueryParts query = new QueryParts("""
                SELECT *
                FROM cheque_reader
                WHERE COALESCE(deleted, B'0') = B'0'
                """);

        query.addEquals("cheque_no", "chequeNo", chequeNo);
        query.addEquals("full_cheque_no", "fullChequeNo", fullChequeNo);
        query.sql.append(" ORDER BY create_time DESC, cheque_no");

        return jdbcTemplate.queryForList(query.sql.toString(), query.params);
    }

    public int createChequeReader(Map<String, Object> request) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bound", requiredString(request, "bound"))
                .addValue("chequeNo", requiredString(request, "chequeNo"))
                .addValue("bankName", optionalString(request, "bankName"))
                .addValue("scanUserId", optionalString(request, "scanUserId"))
                .addValue("lastModified", now())
                .addValue("createTime", now())
                .addValue("autoRead", booleanBit(request.get("autoRead")))
                .addValue("fullChequeNo", optionalString(request, "fullChequeNo"));

        return jdbcTemplate.update("""
                INSERT INTO cheque_reader (
                    bound,
                    cheque_no,
                    bank_name,
                    scan_user_id,
                    last_modified,
                    create_time,
                    auto_read,
                    full_cheque_no
                )
                VALUES (
                    :bound,
                    :chequeNo,
                    :bankName,
                    :scanUserId,
                    :lastModified,
                    :createTime,
                    CAST(:autoRead AS bit(1)),
                    :fullChequeNo
                )
                """, params);
    }

    public int cancelCheque(Map<String, Object> request) {
        String chequeNo = requiredString(request, "chequeNo");
        String fullChequeNo = requiredString(request, "fullChequeNo");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chequeNo", chequeNo)
                .addValue("fullChequeNo", fullChequeNo)
                .addValue("reason", optionalString(request, "reason"))
                .addValue("userId", optionalString(request, "userId"))
                .addValue("actionDate", now());

        int updated = jdbcTemplate.update("""
                UPDATE cheque_reader
                SET deleted = B'1',
                    last_modified = :actionDate
                WHERE cheque_no = :chequeNo
                  AND full_cheque_no = :fullChequeNo
                """, params);

        if (updated > 0) {
            jdbcTemplate.update("""
                    INSERT INTO receipt_auditlog (
                        cancelled_chequeno,
                        reason,
                        user_id,
                        action_date,
                        full_cheque_no
                    )
                    VALUES (
                        :chequeNo,
                        :reason,
                        :userId,
                        :actionDate,
                        :fullChequeNo
                    )
                    """, params);
        }

        return updated;
    }

    public List<Map<String, Object>> getInvoices(String customerName, String vesselName, String voyageNo,
                                                 String referenceNo) {
        QueryParts query = new QueryParts("""
                SELECT *
                FROM invoice
                WHERE 1 = 1
                """);

        query.addEquals("customer_name", "customerName", customerName);
        query.addEquals("vessel_name", "vesselName", vesselName);
        query.addEquals("voyage_no", "voyageNo", voyageNo);
        query.addEquals("reference_no", "referenceNo", referenceNo);
        query.sql.append(" ORDER BY reference_date DESC, reference_no");

        return jdbcTemplate.queryForList(query.sql.toString(), query.params);
    }

    public List<Map<String, Object>> getOutstandingInvoices(String customerName, String vesselName, String voyageNo,
                                                            String referenceNo) {
        QueryParts query = new QueryParts("""
                SELECT *
                FROM partial
                WHERE 1 = 1
                """);

        query.addEquals("customer_name", "customerName", customerName);
        query.addEquals("vessel_name", "vesselName", vesselName);
        query.addEquals("voyage_no", "voyageNo", voyageNo);
        query.addEquals("reference_no", "referenceNo", referenceNo);
        query.sql.append(" ORDER BY transaction_date DESC, reference_no");

        return jdbcTemplate.queryForList(query.sql.toString(), query.params);
    }

    public int removeInvoice(Map<String, Object> request) {
        String referenceNo = requiredString(request, "referenceNo");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("referenceNo", referenceNo)
                .addValue("invoiceSource", optionalString(request, "invoiceSource"))
                .addValue("userId", optionalString(request, "userId"))
                .addValue("actionDate", now())
                .addValue("reason", optionalString(request, "reason"));

        int updated = jdbcTemplate.update("""
                UPDATE invoice
                SET status = B'0'
                WHERE reference_no = :referenceNo
                """, params);

        jdbcTemplate.update("""
                INSERT INTO receipt_auditlog (
                    removed_invoice,
                    invoicesource,
                    user_id,
                    action_date,
                    reason
                )
                VALUES (
                    :referenceNo,
                    :invoiceSource,
                    :userId,
                    :actionDate,
                    :reason
                )
                """, params);

        return updated;
    }

    public List<Map<String, Object>> getDailyScanReport(String fromDate, String toDate, String bound) {
        QueryParts query = new QueryParts("""
                SELECT *
                FROM cheque_reader
                WHERE COALESCE(deleted, B'0') = B'0'
                """);

        query.addDateRange("create_time", "fromDate", fromDate, "toDate", toDate);
        query.addEquals("bound", "bound", bound);
        query.sql.append(" ORDER BY create_time DESC");

        return jdbcTemplate.queryForList(query.sql.toString(), query.params);
    }

    public List<Map<String, Object>> getAgingChequeReport(Integer days) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("days", days == null ? 0 : days);
        return jdbcTemplate.queryForList("""
                SELECT cr.*,
                       DATE_PART('day', NOW() - cr.create_time::timestamp) AS aging
                FROM cheque_reader cr
                WHERE DATE_PART('day', NOW() - cr.create_time::timestamp) >= :days
                  AND COALESCE(cr.deleted, B'0') = B'0'
                  AND cr.cheque_no NOT IN (
                      SELECT DISTINCT reference_no
                      FROM receipt
                      WHERE status = B'1'
                        AND reference_no IS NOT NULL
                  )
                ORDER BY cr.create_time DESC
                """, params);
    }

    public List<Map<String, Object>> getRemovedInvoiceReport(String fromDate, String toDate) {
        return auditReport("""
                SELECT DISTINCT removed_invoice, invoicesource, user_id, action_date, reason
                FROM receipt_auditlog
                WHERE removed_invoice IS NOT NULL
                """, fromDate, toDate);
    }

    public List<Map<String, Object>> getUpdatedReferenceReport(String paymentMode, String fromDate, String toDate) {
        QueryParts query = new QueryParts("""
                SELECT transaction_no, original_chequeno, new_chequeno, user_id, action_date, reason, payment_mode
                FROM receipt_auditlog
                WHERE original_chequeno IS NOT NULL
                  AND new_chequeno IS NOT NULL
                """);
        query.addEquals("payment_mode", "paymentMode", paymentMode);
        query.addDateRange("action_date", "fromDate", fromDate, "toDate", toDate);
        query.sql.append(" ORDER BY action_date DESC");
        return jdbcTemplate.queryForList(query.sql.toString(), query.params);
    }

    public List<Map<String, Object>> getCancelledChequeReport(String fromDate, String toDate) {
        return auditReport("""
                SELECT DISTINCT cancelled_chequeno, full_cheque_no, reason, user_id, action_date
                FROM receipt_auditlog
                WHERE cancelled_chequeno IS NOT NULL
                """, fromDate, toDate);
    }

    private List<Map<String, Object>> auditReport(String baseSql, String fromDate, String toDate) {
        QueryParts query = new QueryParts(baseSql);
        query.addDateRange("action_date", "fromDate", fromDate, "toDate", toDate);
        query.sql.append(" ORDER BY action_date DESC");
        return jdbcTemplate.queryForList(query.sql.toString(), query.params);
    }

    private String now() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    private String booleanBit(Object value) {
        if (value instanceof Boolean bool) {
            return bool ? "1" : "0";
        }
        if (value == null) {
            return "0";
        }
        return Boolean.parseBoolean(value.toString()) ? "1" : "0";
    }

    private String requiredString(Map<String, Object> request, String key) {
        String value = optionalString(request, key);
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value;
    }

    private String optionalString(Map<String, Object> request, String key) {
        Object value = request.get(key);
        if (value == null || !StringUtils.hasText(value.toString())) {
            return null;
        }
        return value.toString().trim();
    }

    private static final class QueryParts {
        private final StringBuilder sql;
        private final MapSqlParameterSource params = new MapSqlParameterSource();

        private QueryParts(String sql) {
            this.sql = new StringBuilder(sql);
        }

        private void addEquals(String column, String param, String value) {
            if (StringUtils.hasText(value)) {
                sql.append(" AND ").append(column).append(" = :").append(param);
                params.addValue(param, value);
            } else {
                params.addValue(param, null);
            }
        }

        private void addDateRange(String column, String fromParam, String fromDate, String toParam, String toDate) {
            if (StringUtils.hasText(fromDate)) {
                sql.append(" AND ").append(column).append(" >= :").append(fromParam);
                params.addValue(fromParam, fromDate + " 00:00:00");
            }
            if (StringUtils.hasText(toDate)) {
                sql.append(" AND ").append(column).append(" <= :").append(toParam);
                params.addValue(toParam, toDate + " 23:59:59");
            }
        }
    }
}
