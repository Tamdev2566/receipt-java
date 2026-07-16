package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.RetrieveRequest;
import com.receipt.receiptPhase.model.RetrieveResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RetrieveService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RetrieveService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RetrieveResponse retrieveData(RetrieveRequest request) {
        RetrieveResponse response = new RetrieveResponse();
        MapSqlParameterSource params = new MapSqlParameterSource();

        String invNo = request.getInvoiceNo() != null ? request.getInvoiceNo().trim() : "";
        String chqNo = request.getChequeNo() != null ? request.getChequeNo().trim() : "";
        String blNo = request.getBlNo() != null ? request.getBlNo().trim() : "";

        if (invNo.isEmpty() && chqNo.isEmpty() && blNo.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Please Enter Invoice No or BL No or Cheque No.");
            return response;
        }

        // --- Screenshots படி சரியான Column பெயர்கள் மற்றும் Aliases (r, i, p) பயன்படுத்தப்பட்டுள்ளன ---

        String strReceipt = "SELECT DISTINCT r.transaction_no, r.transaction_date, r.currency_code, r.amount, r.paid_invoice_total, r.receipt_date, r.reference_no " +
                "FROM receipt r INNER JOIN invoice i ON i.transaction_no = r.transaction_no ";

        String strInvoice = "SELECT DISTINCT i2.transaction_no, i2.bl_no, i2.vessel_code, i2.vessel_name, i2.voyage_no, i2.customer_name, i2.type, i2.reference_date, i2.reference_no, i2.currency, i2.settlement_amt, i2.value_doc as SGD_Amount, i2.value_dual as USD_Amount, i2.original_sgd, i2.original_usd, i2.partial, i2.write_off " +
                "FROM invoice i1 INNER JOIN invoice i2 ON i1.transaction_no = i2.transaction_no INNER JOIN receipt r ON i1.transaction_no = r.transaction_no ";

        // குறிப்பு: DB-ல் உள்ள ஸ்பெல்லிங் மிஸ்டேக் 'parital_status' அப்படியே பயன்படுத்தப்பட்டுள்ளது.
        String strPartial = "SELECT DISTINCT p.transaction_no, p.bl_no, p.type, p.reference_date, p.reference_no, p.currency_code, p.settlement_amount, p.value_doc as SGD_Amount, p.value_dual as USD_Amount, p.original_sgd, p.original_usd, p.parital_status, p.write_off_status " +
                "FROM partial p INNER JOIN receipt r ON p.transaction_no = r.transaction_no ";

        String strCheck = "SELECT r.posted_to_coda, r.status FROM receipt r INNER JOIN invoice i ON i.transaction_no = r.transaction_no ";

        // Postgres BIT(1) க்காக '0' பயன்படுத்தப்பட்டுள்ளது
        String activeCondition = " AND (r.posted_to_coda IS NULL OR r.posted_to_coda = '0') AND (r.status IS NULL OR r.status = '0') ";

        // Condition Appending
        if (!invNo.isEmpty() && blNo.isEmpty() && chqNo.isEmpty()) {
            strReceipt += " WHERE i.reference_no = :invNo " + activeCondition + " ORDER BY r.transaction_no";
            strInvoice += " WHERE i1.reference_no = :invNo " + activeCondition + " ORDER BY i2.transaction_no";
            strPartial += " WHERE p.reference_no = :invNo " + activeCondition + " ORDER BY p.transaction_no";
            strCheck += " WHERE i.reference_no = :invNo ORDER BY r.transaction_no";
            params.addValue("invNo", invNo);
        } else if (blNo.isEmpty() && !chqNo.isEmpty()) {
            strReceipt += " WHERE r.reference_no = :chqNo " + activeCondition + " ORDER BY r.transaction_no";
            strInvoice += " WHERE r.reference_no = :chqNo " + activeCondition + " ORDER BY i2.transaction_no";
            strPartial += " WHERE r.reference_no = :chqNo " + activeCondition + " ORDER BY p.transaction_no";
            strCheck += " WHERE r.reference_no = :chqNo ORDER BY r.transaction_no";
            params.addValue("chqNo", chqNo);
        } else if (!blNo.isEmpty() && chqNo.isEmpty()) {
            strReceipt += " WHERE i.bl_no = :blNo " + activeCondition + " ORDER BY r.transaction_no";
            strInvoice += " WHERE i1.bl_no = :blNo " + activeCondition + " ORDER BY i2.transaction_no";
            strPartial += " WHERE p.bl_no = :blNo " + activeCondition + " ORDER BY p.transaction_no";
            strCheck += " WHERE i.bl_no = :blNo ORDER BY r.transaction_no";
            params.addValue("blNo", blNo);
        } else {
            response.setSuccess(false);
            response.setMessage("Please search by Invoice No or BL No or ChequeNo.");
            return response;
        }

        // Execute Queries
        List<Map<String, Object>> receipts = jdbcTemplate.queryForList(strReceipt, params);
        List<Map<String, Object>> invoices = jdbcTemplate.queryForList(strInvoice, params);

        if (!receipts.isEmpty() && !invoices.isEmpty()) {
            // Get header info from first invoice row (keys are lowercase in DB)
            Map<String, Object> firstInvoice = invoices.get(0);
            Map<String, Object> header = new HashMap<>();
            header.put("BL_No", firstInvoice.get("bl_no"));
            header.put("Vessel_Name", firstInvoice.get("vessel_name"));
            header.put("Voyage_No", firstInvoice.get("voyage_no"));
            header.put("Customer_Name", firstInvoice.get("customer_name"));

            List<Map<String, Object>> outstandings = jdbcTemplate.queryForList(strPartial, params);

            response.setSuccess(true);
            response.setMessage("Data retrieved successfully.");
            response.setHeaderData(header);
            response.setReceipts(receipts);
            response.setInvoices(invoices);
            response.setOutstandings(outstandings);

        } else {
            // Check for errors (Posted to coda or Deleted)
            List<Map<String, Object>> checkResult = jdbcTemplate.queryForList(strCheck, params);
            if (!checkResult.isEmpty()) {
                Map<String, Object> row = checkResult.get(0);

                // Parsing boolean values from DB
                boolean postedToCoda = row.get("posted_to_coda") != null &&
                        (row.get("posted_to_coda").toString().equals("1") || row.get("posted_to_coda").toString().equalsIgnoreCase("true"));

                boolean status = row.get("status") != null &&
                        (row.get("status").toString().equals("1") || row.get("status").toString().equalsIgnoreCase("true"));

                response.setSuccess(false);
                if (postedToCoda) {
                    response.setMessage("It have been posted to coda.");
                } else if (status) {
                    response.setMessage("It have been deleted by users.");
                } else {
                    response.setMessage("No valid records found based on the status.");
                }
            } else {
                response.setSuccess(false);
                response.setMessage("No records found.");
            }
        }

        return response;
    }
}