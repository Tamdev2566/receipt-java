package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.dto.UndoRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class UndoService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UndoService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public void processUndo(UndoRequest request) {

        if (request.getReceipts() == null || request.getReceipts().isEmpty()) {
            throw new IllegalArgumentException("No receipts provided for undo.");
        }

        String currentDate = LocalDateTime.now().format(formatter);

        for (UndoRequest.UndoReceiptItem item : request.getReceipts()) {
            String transNo = item.getTransactionNo();
            Double amount = item.getAmount();
            String currency = item.getCurrency();

            String partialQuery = "SELECT bl_no, reference_no FROM partial WHERE transaction_no = :transNo";
            List<Map<String, Object>> partialRecords = jdbcTemplate.queryForList(partialQuery, new MapSqlParameterSource("transNo", transNo));

            for (Map<String, Object> partial : partialRecords) {
                String blNo = (String) partial.get("bl_no");
                String refNo = (String) partial.get("reference_no");

                String sourceName = (refNo != null && (refNo.startsWith("CI") || refNo.startsWith("I"))) ? "DocSys" : "Doc4All";


                String exRateQuery = "SELECT exchange_rate FROM source_system_records WHERE bl_no = :blNo LIMIT 1";
                List<Map<String, Object>> exRateResult = jdbcTemplate.queryForList(exRateQuery, new MapSqlParameterSource("blNo", blNo));

                double exRate = 0.0;
                if (!exRateResult.isEmpty() && exRateResult.get(0).get("exchange_rate") != null) {
                    exRate = ((Number) exRateResult.get(0).get("exchange_rate")).doubleValue();
                }

                String activeCond = " AND (r.status IS NULL OR r.status = '0')";
                String invCountQuery = "SELECT i.* FROM invoice i INNER JOIN receipt r ON i.transaction_no = r.transaction_no WHERE i.bl_no = :blNo AND i.reference_no = :refNo" + activeCond;
                String partQuery = "SELECT p.* FROM partial p INNER JOIN receipt r ON p.transaction_no = r.transaction_no WHERE p.bl_no = :blNo AND p.reference_no = :refNo" + activeCond + " ORDER BY p.transaction_date ASC";

                MapSqlParameterSource filterParams = new MapSqlParameterSource();
                filterParams.addValue("blNo", blNo);
                filterParams.addValue("refNo", refNo);

                List<Map<String, Object>> invList = jdbcTemplate.queryForList(invCountQuery, filterParams);
                List<Map<String, Object>> partList = jdbcTemplate.queryForList(partQuery, filterParams);

                double valueDoc = 0.0, valueDual = 0.0;


                if (exRate == 0.0) {
                    valueDoc = "SGD".equalsIgnoreCase(currency) ? amount : 0.0;
                    valueDual = "SGD".equalsIgnoreCase(currency) ? 0.0 : amount;
                } else {
                    valueDoc = "SGD".equalsIgnoreCase(currency) ? amount : amount * exRate;
                    valueDual = "SGD".equalsIgnoreCase(currency) ? amount / exRate : amount;
                }

                if (invList.size() == partList.size() && partList.size() > 1) {

                    Map<String, Object> lastPartial = partList.get(partList.size() - 1);
                    String lastTransNo = (String) lastPartial.get("transaction_no");

                    String updatePartial = "UPDATE partial SET settlement_amount = settlement_amount + :amt, value_doc = value_doc + :vDoc, value_dual = value_dual + :vDual, modified_date = :modDate WHERE transaction_no = :transNo";
                    MapSqlParameterSource updateParams = new MapSqlParameterSource();
                    updateParams.addValue("amt", amount);
                    updateParams.addValue("vDoc", valueDoc);
                    updateParams.addValue("vDual", valueDual);
                    updateParams.addValue("modDate", currentDate);
                    updateParams.addValue("transNo", lastTransNo);

                    jdbcTemplate.update(updatePartial, updateParams);

                } else if (!invList.isEmpty()) {

                    Map<String, Object> lastInv = invList.get(invList.size() - 1);
                    String insertPartial = "INSERT INTO partial (transaction_no, transaction_date, source, bl_no, vessel_code, vessel_name, voyage_no, customer_name, type, reference_date, reference_no, currency_code, settlement_amount, value_doc, value_dual, original_sgd, original_usd, parital_status, write_off_status) " +
                            "VALUES (:tNo, :tDate, :src, :bNo, :vCode, :vName, :voyNo, :cName, :type, :rDate, :rNo, :curr, :amt, :vDoc, :vDual, 0, 0, '0', '0')";

                    MapSqlParameterSource insertParams = new MapSqlParameterSource();
                    insertParams.addValue("tNo", lastInv.get("transaction_no"));
                    insertParams.addValue("tDate", currentDate);
                    insertParams.addValue("src", sourceName);
                    insertParams.addValue("bNo", blNo);
                    insertParams.addValue("vCode", lastInv.get("vessel_code"));
                    insertParams.addValue("vName", lastInv.get("vessel_name"));
                    insertParams.addValue("voyNo", lastInv.get("voyage_no"));
                    insertParams.addValue("cName", lastInv.get("customer_name"));
                    insertParams.addValue("type", lastInv.get("type"));
                    insertParams.addValue("rDate", lastInv.get("reference_date"));
                    insertParams.addValue("rNo", lastInv.get("reference_no"));
                    insertParams.addValue("curr", currency);
                    insertParams.addValue("amt", amount);
                    insertParams.addValue("vDoc", valueDoc);
                    insertParams.addValue("vDual", valueDual);

                    jdbcTemplate.update(insertPartial, insertParams);


                    jdbcTemplate.update("UPDATE invoice SET partial = '1' WHERE transaction_no = :tNo", new MapSqlParameterSource("tNo", lastInv.get("transaction_no")));
                }
            }


            String updateReceipt = "UPDATE receipt SET status = '1', modified_date = :modDate WHERE transaction_no = :transNo";
            jdbcTemplate.update(updateReceipt, new MapSqlParameterSource("modDate", currentDate).addValue("transNo", transNo));


            String invoiceQuery = "SELECT bl_no, reference_no FROM invoice WHERE transaction_no = :transNo";
            List<Map<String, Object>> invoices = jdbcTemplate.queryForList(invoiceQuery, new MapSqlParameterSource("transNo", transNo));

            for (Map<String, Object> inv : invoices) {
                String resetIndicator = "UPDATE source_system_records SET indicator = 0 WHERE bl_no = :blNo";
                jdbcTemplate.update(resetIndicator, new MapSqlParameterSource("blNo", inv.get("bl_no")));
            }
        }
    }
}