package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.UndoRequest;
import com.receipt.receiptPhase.repository.UndoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UndoService {

    @Autowired
    private UndoRepository undoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private BigDecimal safeBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        String val = value.toString().trim();
        if (val.isEmpty() || val.equalsIgnoreCase("null") || val.contains("t")) { // "t" மற்றும் பிற பிழைகளை தவிர்க்க
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public UndoRequest retrieveRecords(String invNo, String chequeNo, String blNo) {
        List<Map<String, Object>> receipts = undoRepository.retrieveReceipts(invNo, chequeNo, blNo);
        List<Map<String, Object>> invoices = undoRepository.retrieveInvoices(invNo, chequeNo, blNo);


        if (receipts.isEmpty() || invoices.isEmpty()) {
            return null;
        }

        UndoRequest response = new UndoRequest();
        Map<String, Object> baseRow = invoices.get(0);

        String actualBlNo = Objects.toString(baseRow.getOrDefault("bl_no", baseRow.get("BL_No")), "");
        String actualRefNo = Objects.toString(baseRow.getOrDefault("reference_no", baseRow.get("Reference_No")), "");

        response.setBlNo(actualBlNo);
        response.setVesselName(Objects.toString(baseRow.getOrDefault("vessel_name", baseRow.get("Vessel_Name")), ""));
        response.setVoyageNo(Objects.toString(baseRow.getOrDefault("voyage_no", baseRow.get("Voyage_No")), ""));
        response.setCustomerName(Objects.toString(baseRow.getOrDefault("customer_name", baseRow.get("Customer_Name")), ""));


        List<UndoRequest.ReceiptDTO> receiptList = new java.util.ArrayList<>();
        for (Map<String, Object> r : receipts) {
            UndoRequest.ReceiptDTO dto = new UndoRequest.ReceiptDTO();
            dto.transactionNo = Objects.toString(r.getOrDefault("transaction_no", r.get("Transaction_No")), "");
            dto.referenceNo = Objects.toString(r.getOrDefault("reference_no", r.get("Reference_No")), "");
            dto.currency = Objects.toString(r.getOrDefault("currency", r.get("Currency")), "");


            dto.amount = new BigDecimal(Objects.toString(r.getOrDefault("amount", r.get("Amount")), "0"));
            dto.paidInvoiceTotal = new BigDecimal(Objects.toString(r.getOrDefault("paid_invoice_total", r.get("Paid_Invoice_Total")), "0"));


            dto.transactionDate = Objects.toString(r.getOrDefault("transaction_date", r.get("Transaction_Date")), "");
            dto.receiptDate = Objects.toString(r.getOrDefault("receipt_date", r.get("Receipt_Date")), "");

            receiptList.add(dto);
        }
        response.setReceipts(receiptList);

        List<UndoRequest.InvoiceDTO> invoiceList = new java.util.ArrayList<>();
        for (Map<String, Object> i : invoices) {
            UndoRequest.InvoiceDTO dto = new UndoRequest.InvoiceDTO();
            dto.transactionNo = Objects.toString(i.getOrDefault("transaction_no", i.get("Transaction_No")), "");
            dto.transactionDate = Objects.toString(i.getOrDefault("transaction_date", i.get("Transaction_date")), "");
            dto.type = Objects.toString(i.getOrDefault("type", i.get("Type")), "");
            dto.referenceNo = Objects.toString(i.getOrDefault("reference_no", i.get("Reference_No")), "");
            dto.currency = Objects.toString(i.getOrDefault("currency", i.get("Currency")), "");
            dto.settlementAmt = safeBigDecimal(i.get("Settlement_Amt"));
            dto.sgdAmount = safeBigDecimal(i.get("SGD_Amount"));
            dto.usdAmount = safeBigDecimal(i.get("USD_Amount"));
            dto.originalsgdAmount = safeBigDecimal(i.get("original_sgd"));
            dto.originalusdAmount = safeBigDecimal(i.get("original_usd"));
            dto.partial = safeBigDecimal(i.get("partial"));
            dto.writeOff = safeBigDecimal(i.get("write_off"));

            invoiceList.add(dto);
        }
        response.setInvoices(invoiceList);


        List<Map<String, Object>> partials = undoRepository.getPartialDetails(actualRefNo);
        List<UndoRequest.PartialDTO> partialList = new java.util.ArrayList<>();

        if (partials != null && !partials.isEmpty()) {
            for (Map<String, Object> p : partials) {
                UndoRequest.PartialDTO dto = new UndoRequest.PartialDTO();
                dto.transactionNo = Objects.toString(p.getOrDefault("transaction_no", p.get("Transaction_No")), "");
                dto.type = Objects.toString(p.getOrDefault("type", p.get("Type")), "");
                dto.referenceNo = Objects.toString(p.getOrDefault("reference_no", p.get("Reference_No")), "");


                dto.currency = Objects.toString(p.getOrDefault("currency_code", p.get("Currency_Code")), "");

                dto.settlementAmt = new BigDecimal(Objects.toString(p.getOrDefault("settlement_amt", p.get("Settlement_Amt")), "0"));
                dto.sgdAmount = new BigDecimal(Objects.toString(p.getOrDefault("value_doc", p.get("Value_doc")), "0"));
                dto.usdAmount = new BigDecimal(Objects.toString(p.getOrDefault("value_dual", p.get("Value_dual")), "0"));

                partialList.add(dto);
            }
        }
        response.setOutstandings(partialList);

        return response;
    }


    @Transactional
    public void processUndoPayment(List<String> transactionNumbers) {
        for (String transNo : transactionNumbers) {


            String receiptQuery = "SELECT Amount, currency_code as Currency, Reference_No FROM Receipt WHERE Transaction_No = ?";
            Map<String, Object> receiptMap = jdbcTemplate.queryForMap(receiptQuery, transNo);

            BigDecimal amount = new BigDecimal(receiptMap.getOrDefault("amount", receiptMap.get("Amount")).toString());
            String currency = Objects.toString(receiptMap.getOrDefault("currency", receiptMap.get("Currency")), "");

            String partialQuery = "SELECT BL_No, Reference_No FROM Partial WHERE Transaction_No = ?";
            List<Map<String, Object>> partialRows = jdbcTemplate.queryForList(partialQuery, transNo);

            for (Map<String, Object> pRow : partialRows) {
                String blNo = Objects.toString(pRow.getOrDefault("bl_no", pRow.get("BL_No")), "");


                String rateQuery = "SELECT exchange_rate FROM source_system_records WHERE BL_NO = ?";
                BigDecimal exRate = jdbcTemplate.queryForObject(rateQuery, BigDecimal.class, blNo);

                BigDecimal valueDoc = BigDecimal.ZERO;
                BigDecimal valueDual = BigDecimal.ZERO;

                if (exRate != null && exRate.compareTo(BigDecimal.ZERO) != 0) {
                    valueDoc = "SGD".equals(currency) ? amount : amount.multiply(exRate);
                    valueDual = "SGD".equals(currency) ? amount.divide(exRate, 2, RoundingMode.HALF_UP) : amount;
                } else {
                    if ("SGD".equals(currency)) valueDoc = amount; else valueDual = amount;
                }

                String updatePartialSql = "UPDATE Partial SET Settlement_Amt = Settlement_Amt + ?, Value_doc = Value_doc + ?, Value_dual = Value_dual + ?, Transaction_Date = ? WHERE Transaction_No = ?";
                jdbcTemplate.update(updatePartialSql, amount, valueDoc, valueDual, LocalDateTime.now(), transNo);
            }


            String formattedDate = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            undoRepository.softDeleteReceipt(transNo, formattedDate);


            String invQuery = "SELECT BL_No, Reference_No FROM Invoice WHERE Transaction_No = ?";
            List<Map<String, Object>> invoiceRows = jdbcTemplate.queryForList(invQuery, transNo);

            for (Map<String, Object> invRow : invoiceRows) {
                String blNo = Objects.toString(invRow.getOrDefault("bl_no", invRow.get("BL_No")), "");
                String refNo = Objects.toString(invRow.getOrDefault("reference_no", invRow.get("Reference_No")), "");

                String checkActiveReceipts = "SELECT COUNT(*) FROM Invoice i INNER JOIN Receipt r ON i.Transaction_No = r.Transaction_No WHERE (r.Status = '0' OR r.Status IS NULL) AND i.BL_No = ? AND i.Reference_No = ?";
                Integer activeCount = jdbcTemplate.queryForObject(checkActiveReceipts, Integer.class, blNo, refNo);

                if (activeCount != null && activeCount == 0) {

                    String resetIndicator = "UPDATE source_system_records SET Indicator = '0' WHERE BL_No = ? AND Reference_No = ?";
                    jdbcTemplate.update(resetIndicator, blNo, refNo);
                }
            }
        }
    }
}