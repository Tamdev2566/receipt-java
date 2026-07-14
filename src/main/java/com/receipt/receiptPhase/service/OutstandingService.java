package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.dto.OutstandingRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class OutstandingService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OutstandingService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getOutstandingData(OutstandingRequest request) {

        if (request.getCustomerNames() == null || request.getCustomerNames().isEmpty()) {
            return Collections.emptyList();
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("customerNames", request.getCustomerNames());

        String query = "SELECT bl_no, vessel_code, vessel_name, voyage_no, customer_name, type, reference_date, reference_no, " +
                "value_doc AS SGD_Amount, value_dual AS USD_Amount, original_sgd, original_usd " +
                "FROM source_system_records " +
                "WHERE (indicator IS NULL OR indicator = 0) AND customer_name IN (:customerNames)";

        String requestSource = request.getSource();
        if ("DocSys".equalsIgnoreCase(requestSource)) {
            query += " AND source = 'DocSys'";
        }
        else if ("Glossys".equalsIgnoreCase(requestSource) || "Glosys".equalsIgnoreCase(requestSource)) {

            query += " AND source IN ('Glossys', 'Glosys', 'Doc4All')";
        }
        query += " ORDER BY reference_date";

        return jdbcTemplate.queryForList(query, params);
    }
}