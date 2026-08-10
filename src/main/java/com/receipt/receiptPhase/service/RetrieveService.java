package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.RetrieveRequest;
import com.receipt.receiptPhase.model.RetrieveResponse;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RetrieveService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RetrieveService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RetrieveResponse retrieveAllData(RetrieveRequest request) {
        RetrieveResponse response = new RetrieveResponse();
        MapSqlParameterSource params = new MapSqlParameterSource();

        String invNo = request.getInvoiceNo() != null ? request.getInvoiceNo().trim() : "";
        String blNo = request.getBlNo() != null ? request.getBlNo().trim() : "";
        String vessel = request.getVesselName() != null ? request.getVesselName().trim() : "";
        String voyage = request.getVoyageNo() != null ? request.getVoyageNo().trim() : "";
        String customer = request.getCustomerName() != null ? request.getCustomerName().trim() : "";


        if (invNo.isEmpty() && blNo.isEmpty() && vessel.isEmpty() && voyage.isEmpty() && customer.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Please enter search criteria to search.");
            return response;
        }

        if (!vessel.isEmpty() && voyage.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Please choose the voyage no.");
            return response;
        }

        StringBuilder queryMain = new StringBuilder(
                "SELECT DISTINCT bl_no, vessel_code, vessel_name, voyage_no, customer_name, type, reference_date, reference_no, " +
                        "value_doc AS SGD_Amount, value_dual AS USD_Amount, original_sgd, original_usd, exchange_rate " +
                        "FROM source_system_records WHERE (indicator IS NULL OR indicator = 0) "
        );

        StringBuilder queryPartial = new StringBuilder(
                "SELECT DISTINCT p.bl_no, p.reference_no " +
                        "FROM partial p INNER JOIN receipt r ON p.transaction_no = r.transaction_no " +
                        "WHERE (r.status IS NULL OR r.status = '0') "
        );

        StringBuilder queryPartialExclusion = new StringBuilder(
                " AND p.reference_no NOT IN (" +
                        "SELECT i.reference_no FROM invoice i INNER JOIN receipt r ON i.transaction_no = r.transaction_no " +
                        "WHERE (r.status IS NULL OR r.status = '0') AND i.partial = '0' "
        );

        StringBuilder queryCheck = new StringBuilder(
                "SELECT indicator FROM source_system_records WHERE 1=1 "
        );

        // 3. Dynamic Filtering Conditions
        List<String> conditionsMain = new ArrayList<>();
        List<String> conditionsPartial = new ArrayList<>();
        List<String> conditionsCheck = new ArrayList<>();

        if (!invNo.isEmpty()) {
            conditionsMain.add("reference_no = :invNo");
            conditionsPartial.add("p.reference_no = :invNo");
            conditionsCheck.add("reference_no = :invNo");
            params.addValue("invNo", invNo);
        }

        if (!blNo.isEmpty()) {
            conditionsMain.add("bl_no LIKE :blNo");
            conditionsPartial.add("p.bl_no LIKE :blNo");
            conditionsCheck.add("bl_no LIKE :blNo");
            params.addValue("blNo", "%" + blNo + "%");
        }

        if (!vessel.isEmpty() && !voyage.isEmpty()) {
            conditionsMain.add("vessel_name = :vessel AND voyage_no = :voyage");
            conditionsPartial.add("p.vessel_name = :vessel AND p.voyage_no = :voyage");
            conditionsCheck.add("vessel_name = :vessel AND voyage_no = :voyage");
            params.addValue("vessel", vessel);
            params.addValue("voyage", voyage);
        }

        if (!customer.isEmpty()) {
            conditionsMain.add("customer_name LIKE :customer");
            conditionsPartial.add("p.customer_name LIKE :customer");
            conditionsCheck.add("customer_name LIKE :customer");
            params.addValue("customer", "%" + customer + "%");
        }

        // Construct Where Clauses
        if (!conditionsMain.isEmpty()) {
            String whereMain = " AND " + String.join(" AND ", conditionsMain);
            queryMain.append(whereMain);

            String wherePartial = " AND " + String.join(" AND ", conditionsPartial);
            queryPartial.append(wherePartial);
            queryPartialExclusion.append(wherePartial.replace("p.", "i.")).append(")");
            queryPartial.append(queryPartialExclusion);

            queryCheck.append(" AND ").append(String.join(" AND ", conditionsCheck));
        }

        queryMain.append(" ORDER BY reference_date");
        queryCheck.append(" LIMIT 1"); // PostgreSQL syntax for top 1

        // Special condition logic for Invoice-only search
        if (!invNo.isEmpty() && blNo.isEmpty() && vessel.isEmpty() && customer.isEmpty()) {
            String checkBLSql = "SELECT bl_no FROM source_system_records WHERE reference_no = :invNo ORDER BY reference_no";
            List<String> blList = jdbcTemplate.queryForList(checkBLSql, params, String.class);

            if (!blList.isEmpty() && blList.get(0) != null && !blList.get(0).trim().isEmpty()) {
                queryMain = new StringBuilder(
                        "SELECT DISTINCT doc2.bl_no, doc2.vessel_code, doc2.vessel_name, doc2.voyage_no, doc2.customer_name, " +
                                "doc2.type, doc2.reference_date, doc2.reference_no, doc2.value_doc AS SGD_Amount, doc2.value_dual AS USD_Amount, " +
                                "doc2.original_sgd, doc2.original_usd, doc2.exchange_rate " +
                                "FROM source_system_records doc1 INNER JOIN source_system_records doc2 ON doc1.bl_no = doc2.bl_no " +
                                "WHERE (doc1.indicator IS NULL OR doc1.indicator = 0) " +
                                "AND (doc2.indicator IS NULL OR doc2.indicator = 0) " +
                                "AND doc1.bl_no <> '' AND doc1.reference_no = :invNo ORDER BY doc2.reference_date"
                );
            }
        }

        // 4. Execution
        List<Map<String, Object>> mainRecords = jdbcTemplate.queryForList(queryMain.toString(), params);
        List<Map<String, Object>> partialRows = jdbcTemplate.queryForList(queryPartial.toString(), params);

        List<Map<String, Object>> outstandings = new ArrayList<>();
        for (Map<String, Object> row : partialRows) {
            String partialSql = "SELECT transaction_no, bl_no, vessel_code, vessel_name, voyage_no, customer_name, type, " +
                    "reference_date, reference_no, value_doc AS SGD_Amount, value_dual AS USD_Amount, original_sgd, original_usd " +
                    "FROM partial WHERE bl_no = :pBlNo AND reference_no = :pRefNo ORDER BY transaction_date DESC LIMIT 1";
            MapSqlParameterSource pParams = new MapSqlParameterSource();
            pParams.addValue("pBlNo", row.get("bl_no"));
            pParams.addValue("pRefNo", row.get("reference_no"));

            List<Map<String, Object>> res = jdbcTemplate.queryForList(partialSql, pParams);
            if (!res.isEmpty()) {
                outstandings.add(res.get(0));
            }
        }

        // 5. Build Response
        if (!mainRecords.isEmpty() || !outstandings.isEmpty()) {
            Map<String, Object> firstRow = !mainRecords.isEmpty() ? mainRecords.get(0) : outstandings.get(0);

            Map<String, Object> header = new HashMap<>();
            header.put("BL_No", firstRow.get("bl_no"));
            header.put("Vessel_Name", firstRow.get("vessel_name"));
            header.put("Voyage_No", firstRow.get("voyage_no"));
            header.put("Customer_Name", firstRow.get("customer_name"));

            response.setSuccess(true);
            response.setMessage("Data retrieved successfully.");
            response.setHeaderData(header);
            response.setInvoices(mainRecords);
            response.setOutstandings(outstandings);

        } else {
            List<Map<String, Object>> checkResult = jdbcTemplate.queryForList(queryCheck.toString(), params);

            response.setSuccess(false);
            if (!checkResult.isEmpty()) {
                Object ind = checkResult.get(0).get("indicator");
                boolean indicator = ind != null && ind.toString().equals("1");

                if (indicator) {
                    response.setMessage("The payment has been made.");
                } else {
                    response.setMessage("No records found.");
                }
            } else {
                response.setMessage("No records found.");
            }
        }

        return response;
    }
}