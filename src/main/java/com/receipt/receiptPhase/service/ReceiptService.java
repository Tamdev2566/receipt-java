package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.repository.ReceiptRepository;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReceiptService {

    private static final DateTimeFormatter TRANSACTION_DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter DB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ReceiptRepository receiptRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ReceiptService(ReceiptRepository receiptRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.receiptRepository = receiptRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Map<String, Object> confirmPayment(Map<String, Object> request) {
        validatePaymentRequest(request);

        BigDecimal receiptTotal = decimalValue(request, "receiptTotal", "receipt_total", "amount");
        BigDecimal bankCharge = decimalValueOrZero(request, "bankCharge", "bank_charge");
        BigDecimal paidInvoiceTotal = decimalValue(request, "paidInvoiceTotal", "paid_invoice_total", "invoiceTotal");
        if (receiptTotal.add(bankCharge).compareTo(paidInvoiceTotal) > 0) {
            throw badRequest("Over payment detected. Please use /api/receipts/over-payment.");
        }

        validateConfirmBalance(request, receiptTotal, bankCharge, paidInvoiceTotal);
        return savePayment(request, false);
    }

    @Transactional
    public Map<String, Object> overPayment(Map<String, Object> request) {
        validatePaymentRequest(request);
        return savePayment(request, true);
    }

    public List<Map<String, Object>> getAllReceiptsWithActions() {
        List<ReceiptModal> receipts = receiptRepository.findAllReceipts();
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (ReceiptModal receipt : receipts) {
            Map<String, Object> map = new HashMap<>();
            map.put("transactionNo", receipt.getTransactionNo());
            map.put("transactionDate", receipt.getTransactionDate());
            map.put("officeCode", receipt.getOfficeCode());
            map.put("paymentMode", receipt.getPaymentMode());
            map.put("referenceNo", receipt.getReferenceNo());
            map.put("currencyCode", receipt.getCurrencyCode());
            map.put("amount", receipt.getAmount());
            map.put("status", receipt.getStatus());
            map.put("postedToCoda", receipt.getPostedToCoda());


            map.put("createdUser", receipt.getCreatedUser());
            map.put("createdDate", receipt.getCreatedDate());


            boolean isPaidActive = (receipt.getStatus() == null || !receipt.getStatus())
                    && (receipt.getPostedToCoda() == null || !receipt.getPostedToCoda());

            if (isPaidActive) {
                map.put("availableAction", "UNDO_PAYMENT");
                map.put("actionMessage", "Can perform Undo Payment");
            } else if (Boolean.TRUE.equals(receipt.getStatus())) {
                map.put("availableAction", "DELETED");
                map.put("actionMessage", "Payment already undone/cancelled");
            } else {
                map.put("availableAction", "POSTED_TO_CODA");
                map.put("actionMessage", "Cannot undo, already posted to Coda");
            }

            responseList.add(map);
        }
        return responseList;
    }

    private Map<String, Object> savePayment(Map<String, Object> request, boolean overPayment) {
        String transactionNo = nextTransactionNo();
        String now = LocalDateTime.now().format(DB_DATE_FORMAT);

        ReceiptModal receipt = buildReceipt(request, transactionNo, now);
        receiptRepository.insertReceipt(
                receipt.getTransactionNo(),
                receipt.getTransactionDate(),
                receipt.getOfficeCode(),
                receipt.getPaymentMode(),
                receipt.getReceiptDate(),
                receipt.getReferenceNo(),
                receipt.getCurrencyCode(),
                receipt.getAmount(),
                receipt.getBankCharge(),
                receipt.getPaidInvoiceTotal(),
                receipt.getReceiptTotal(),
                receipt.getBalanceAmount(),
                bitValue(receipt.getPostedToCoda()),
                bitValue(receipt.getStatus()),
                receipt.getBank(),
                receipt.getCreatedDate(),
                receipt.getCreatedUser(),
                receipt.getModifiedDate(),
                receipt.getModifiedUser()
        );

        List<Map<String, Object>> invoices = invoiceRows(request);
        int invoiceCount = 0;
        int partialCount = 0;
        for (Map<String, Object> invoice : invoices) {
            if (!booleanValue(invoice, "selected", "checkBox", "CheckBox")) {
                continue;
            }
            invoice.putIfAbsent("officeCode", receipt.getOfficeCode());
            invoice.putIfAbsent("createdDate", receipt.getCreatedDate());
            invoice.putIfAbsent("createdUser", receipt.getCreatedUser());
            insertInvoice(transactionNo, now, invoice);
            updateSourceIndicator(invoice);
            invoiceCount++;

            if (booleanValue(invoice, "partial", "Partial")) {
                insertPartial(transactionNo, now, invoice);
                partialCount++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("transactionNo", transactionNo);
        response.put("overPayment", overPayment);
        response.put("invoiceCount", invoiceCount);
        response.put("partialCount", partialCount);
        response.put("message", "Payment confirmed successfully. Transaction No: " + transactionNo);
        return response;
    }

    private ReceiptModal buildReceipt(Map<String, Object> request, String transactionNo, String now) {
        ReceiptModal receipt = new ReceiptModal();
        receipt.setTransactionNo(transactionNo);
        receipt.setTransactionDate(now);
        receipt.setOfficeCode(requiredString(request, "officeCode", "office_code"));
        receipt.setPaymentMode(requiredString(request, "paymentMode", "payment_mode"));
        receipt.setReceiptDate(defaultString(request, now, "receiptDate", "receipt_date"));
        receipt.setReferenceNo(requiredString(request, "referenceNo", "reference_no", "chequeNo", "ttNo"));
        receipt.setCurrencyCode(requiredString(request, "currencyCode", "currency_code", "currency"));
        receipt.setAmount(decimalValue(request, "amount"));
        receipt.setBankCharge(decimalValueOrZero(request, "bankCharge", "bank_charge"));
        receipt.setPaidInvoiceTotal(decimalValue(request, "paidInvoiceTotal", "paid_invoice_total", "invoiceTotal"));
        receipt.setReceiptTotal(decimalValue(request, "receiptTotal", "receipt_total", "amount"));
        receipt.setBalanceAmount(decimalValueOrZero(request, "balanceAmount", "balance_amount", "balance"));
        receipt.setPostedToCoda(booleanValue(request, "postedToCoda", "posted_to_coda"));
        receipt.setStatus(false);
        receipt.setBank(requiredString(request, "bank"));
        receipt.setCreatedDate(now);
        receipt.setCreatedUser(stringValue(request, "createdUser", "created_user", "userId", "user_id"));
        receipt.setModifiedDate(now);
        receipt.setModifiedUser(stringValue(request, "modifiedUser", "modified_user", "userId", "user_id"));
        return receipt;
    }

    private void insertInvoice(String transactionNo, String now, Map<String, Object> invoice) {
        MapSqlParameterSource params = baseInvoiceParams(transactionNo, now, invoice);
        jdbcTemplate.update("""
                INSERT INTO invoice (
                    transaction_no,
                    transaction_date,
                    office_code,
                    bl_no,
                    vessel_code,
                    vessel_name,
                    voyage_no,
                    customer_name,
                    type,
                    reference_date,
                    reference_no,
                    currency,
                    settlement_amt,
                    value_doc,
                    value_dual,
                    original_sgd,
                    original_usd,
                    partial,
                    write_off,
                    created_date,
                    created_user
                )
                VALUES (
                    :transactionNo,
                    :transactionDate,
                    :officeCode,
                    :blNo,
                    :vesselCode,
                    :vesselName,
                    :voyageNo,
                    :customerName,
                    :type,
                    :referenceDate,
                    :referenceNo,
                    :currency,
                    :settlementAmount,
                    :valueDoc,
                    :valueDual,
                    :originalSgd,
                    :originalUsd,
                    CAST(:partial AS bit(1)),
                    CAST(:writeOff AS bit(1)),
                    :createdDate,
                    :createdUser
                )
                """, params);
    }

    private void insertPartial(String transactionNo, String now, Map<String, Object> invoice) {
        BigDecimal oldSettlementAmount = decimalValueOrZero(invoice, "oldSettlementAmount", "OldSettlementAmount");
        BigDecimal newSettlementAmount = decimalValueOrZero(invoice, "settlementAmount", "SettlementAmount", "settlement_amt");
        BigDecimal outstandingAmount = oldSettlementAmount.subtract(newSettlementAmount).abs();
        BigDecimal exchangeRate = decimalValueOrZero(invoice, "exchangeRate", "exrate", "Exrate");
        String currency = stringValue(invoice, "currency", "Currency");

        BigDecimal valueDoc;
        BigDecimal valueDual;
        if ("SGD".equalsIgnoreCase(currency)) {
            valueDoc = outstandingAmount;
            valueDual = BigDecimal.ZERO.compareTo(exchangeRate) == 0 ? BigDecimal.ZERO : outstandingAmount.divide(exchangeRate, 4, RoundingMode.HALF_UP);
        } else {
            valueDoc = BigDecimal.ZERO.compareTo(exchangeRate) == 0 ? BigDecimal.ZERO : outstandingAmount.multiply(exchangeRate);
            valueDual = outstandingAmount;
        }

        MapSqlParameterSource params = baseInvoiceParams(transactionNo, now, invoice)
                .addValue("settlementAmount", outstandingAmount)
                .addValue("valueDoc", valueDoc)
                .addValue("valueDual", valueDual)
                .addValue("originalSgd", BigDecimal.ZERO)
                .addValue("originalUsd", BigDecimal.ZERO)
                .addValue("partial", "0")
                .addValue("writeoff", "0")
                .addValue("source", sourceFor(invoice));

        jdbcTemplate.update("""
                INSERT INTO partial (
                    transaction_no,
                    transaction_date,
                    source,
                    bl_no,
                    vessel_code,
                    vessel_name,
                    voyage_no,
                    customer_name,
                    type,
                    reference_date,
                    reference_no,
                    currency,
                    settlement_amt,
                    value_doc,
                    value_dual,
                    original_sgd,
                    original_usd,
                    partial,
                    writeoff
                )
                VALUES (
                    :transactionNo,
                    :transactionDate,
                    :source,
                    :blNo,
                    :vesselCode,
                    :vesselName,
                    :voyageNo,
                    :customerName,
                    :type,
                    :referenceDate,
                    :referenceNo,
                    :currency,
                    :settlementAmount,
                    :valueDoc,
                    :valueDual,
                    :originalSgd,
                    :originalUsd,
                    CAST(:partial AS bit(1)),
                    CAST(:writeoff AS bit(1))
                )
                """, params);
    }

    private MapSqlParameterSource baseInvoiceParams(String transactionNo, String now, Map<String, Object> invoice) {
        return new MapSqlParameterSource()
                .addValue("transactionNo", transactionNo)
                .addValue("transactionDate", now)
                .addValue("officeCode", stringValue(invoice, "officeCode", "office_code"))
                .addValue("blNo", stringValue(invoice, "blNo", "bl_no", "BLNo"))
                .addValue("vesselCode", stringValue(invoice, "vesselCode", "vessel_code", "VesselCode"))
                .addValue("vesselName", stringValue(invoice, "vesselName", "vessel_name", "VesselName"))
                .addValue("voyageNo", stringValue(invoice, "voyageNo", "voyage_no", "VoyageNo"))
                .addValue("customerName", stringValue(invoice, "customerName", "customer_name", "CustomerName"))
                .addValue("type", stringValue(invoice, "type", "Type"))
                .addValue("referenceDate", stringValue(invoice, "referenceDate", "reference_date", "ReferenceDate"))
                .addValue("referenceNo", stringValue(invoice, "referenceNo", "reference_no", "ReferenceNo"))
                .addValue("currency", stringValue(invoice, "currency", "Currency"))
                .addValue("settlementAmount", decimalValueOrZero(invoice, "settlementAmount", "SettlementAmount", "settlement_amt"))
                .addValue("valueDoc", decimalValueOrZero(invoice, "valueDoc", "SGDAmount", "SGD_Amount", "value_doc"))
                .addValue("valueDual", decimalValueOrZero(invoice, "valueDual", "USDAmount", "USD_Amount", "value_dual"))
                .addValue("originalSgd", decimalValueOrZero(invoice, "originalSgd", "OriginalSGD", "original_sgd"))
                .addValue("originalUsd", decimalValueOrZero(invoice, "originalUsd", "OriginalUSD", "original_usd"))
                .addValue("partial", bitValue(booleanValue(invoice, "partial", "Partial")))
                .addValue("writeOff", bitValue(booleanValue(invoice, "writeoff", "writeOff", "write_off", "Write-off")))
                .addValue("createdDate", defaultString(invoice, now, "createdDate", "created_date"))
                .addValue("createdUser", stringValue(invoice, "createdUser", "created_user", "userId", "user_id"));
    }

    private void updateSourceIndicator(Map<String, Object> invoice) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("blNo", stringValue(invoice, "blNo", "bl_no", "BLNo"))
                .addValue("referenceNo", stringValue(invoice, "referenceNo", "reference_no", "ReferenceNo"))
                .addValue("source", sourceFor(invoice));

        jdbcTemplate.update("""
                UPDATE source_system_records
                SET indicator = 1
                WHERE bl_no = :blNo
                  AND reference_no = :referenceNo
                  AND (:source IS NULL OR source = :source)
                """, params);
    }

    private void validatePaymentRequest(Map<String, Object> request) {
        requiredString(request, "paymentMode", "payment_mode");
        requiredString(request, "officeCode", "office_code");
        requiredString(request, "referenceNo", "reference_no", "chequeNo", "ttNo");
        requiredString(request, "currencyCode", "currency_code", "currency");
        requiredString(request, "bank");

        if (decimalValue(request, "amount").compareTo(BigDecimal.ZERO) <= 0) {
            throw badRequest("Please enter a valid amount.");
        }
        if (invoiceRows(request).stream().noneMatch(row -> booleanValue(row, "selected", "checkBox", "CheckBox"))) {
            throw badRequest("Please select at least one reference to pay.");
        }
    }

    private void validateConfirmBalance(Map<String, Object> request, BigDecimal receiptTotal, BigDecimal bankCharge, BigDecimal paidInvoiceTotal) {
        BigDecimal balance = decimalValueOrZero(request, "balanceAmount", "balance_amount", "balance");
        if (receiptTotal.add(bankCharge).compareTo(paidInvoiceTotal) < 0) {
            if (balance.compareTo(BigDecimal.ONE) <= 0 && !hasWriteoff(invoiceRows(request))) {
                throw badRequest("Please check write-off for this amount.");
            }
            if (balance.compareTo(BigDecimal.ONE) > 0 && !hasPartial(invoiceRows(request))) {
                throw badRequest("Invalid balance to make payment. Please make partial payment.");
            }
        }
    }

    private String nextTransactionNo() {
        String datePrefix = LocalDateTime.now().format(TRANSACTION_DATE_FORMAT);
        String maxNo = receiptRepository.findMaxTransactionNoForDate(datePrefix + "%");

        int nextNo = 1;
        if (maxNo != null && maxNo.length() >= 4) {
            nextNo = Integer.parseInt(maxNo.substring(maxNo.length() - 4)) + 1;
        }
        return datePrefix + String.format("%04d", nextNo);
    }

    private List<Map<String, Object>> invoiceRows(Map<String, Object> request) {
        Object rows = firstPresent(request, "invoices", "invoiceRows", "selectedInvoices");
        if (!(rows instanceof List<?> list)) {
            return List.of();
        }

        List<Map<String, Object>> invoices = new ArrayList<>();
        for (Object row : list) {
            if (row instanceof Map<?, ?> map) {
                Map<String, Object> invoice = new HashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() != null) {
                        invoice.put(entry.getKey().toString(), entry.getValue());
                    }
                }
                invoices.add(invoice);
            }
        }
        return invoices;
    }

    private boolean hasPartial(List<Map<String, Object>> invoices) {
        return invoices.stream().anyMatch(row -> booleanValue(row, "partial", "Partial"));
    }

    private boolean hasWriteoff(List<Map<String, Object>> invoices) {
        return invoices.stream().anyMatch(row -> booleanValue(row, "writeoff", "writeOff", "Write-off"));
    }

    private String sourceFor(Map<String, Object> invoice) {
        String source = stringValue(invoice, "source", "Source");
        if (source != null) {
            return source;
        }
        String referenceNo = stringValue(invoice, "referenceNo", "reference_no", "ReferenceNo");
        if (referenceNo != null && (referenceNo.startsWith("CI") || referenceNo.startsWith("I"))) {
            return "DocSys";
        }
        return "Doc4All";
    }

    private String bitValue(Boolean value) {
        return Boolean.TRUE.equals(value) ? "1" : "0";
    }

    private String requiredString(Map<String, Object> values, String... keys) {
        String value = stringValue(values, keys);
        if (value == null || value.isBlank() || "Choose".equalsIgnoreCase(value)) {
            throw badRequest("Missing required field: " + keys[0]);
        }
        return value;
    }

    private String defaultString(Map<String, Object> values, String defaultValue, String... keys) {
        String value = stringValue(values, keys);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private String stringValue(Map<String, Object> values, String... keys) {
        Object value = firstPresent(values, keys);
        return value == null ? null : value.toString().trim();
    }

    private BigDecimal decimalValue(Map<String, Object> values, String... keys) {
        Object value = firstPresent(values, keys);
        if (value == null || value.toString().isBlank()) {
            throw badRequest("Missing required field: " + keys[0]);
        }
        return toBigDecimal(value);
    }

    private BigDecimal decimalValueOrZero(Map<String, Object> values, String... keys) {
        Object value = firstPresent(values, keys);
        if (value == null || value.toString().isBlank()) {
            return BigDecimal.ZERO;
        }
        return toBigDecimal(value);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        String normalized = value.toString()
                .trim()
                .replace(",", "")
                .replace("(", "")
                .replace(")", "");
        return new BigDecimal(normalized);
    }

    private Boolean booleanValue(Map<String, Object> values, String... keys) {
        Object value = firstPresent(values, keys);
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value == null) {
            return false;
        }
        String normalized = value.toString().trim().toLowerCase(Locale.ROOT);
        return "true".equals(normalized) || "1".equals(normalized) || "yes".equals(normalized);
    }

    private Object firstPresent(Map<String, Object> values, String... keys) {
        for (String key : keys) {
            if (values.containsKey(key)) {
                return values.get(key);
            }
        }
        return null;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
