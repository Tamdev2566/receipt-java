package com.receipt.receiptPhase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chequeBox")
public class ChequeBoxController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/cheque-numbers")
    public ResponseEntity<List<Map<String, Object>>> searchChequeNumbers(@RequestBody Map<String, String> payload) {
        String keyword = payload.getOrDefault("search", "");

        String sql = "SELECT DISTINCT CHEQUE_NO FROM CHEQUE_READER " +
                "WHERE CHEQUE_NO <> '' " +
                "AND CHEQUE_NO LIKE ? " +
                "ORDER BY CHEQUE_NO";

        String param = "%" + keyword + "%";


        List<Map<String, Object>> results = jdbcTemplate.query(sql, new Object[]{param}, (rs, rowNum) -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", rowNum + 1); // வரிசை எண்
            map.put("name", rs.getString("CHEQUE_NO"));
            return map;
        });

        return ResponseEntity.ok(results);
    }

    @PostMapping("/full-cheque-numbers")
    public ResponseEntity<List<Map<String, Object>>> searchFullChequeNumbers(@RequestBody Map<String, String> payload) {
        String chequeNo = payload.get("chequeNo");
        String fullChequeKeyword = payload.getOrDefault("search", "");

        String sql = "SELECT DISTINCT FULL_CHEQUE_NO FROM CHEQUE_READER " +
                "WHERE CHEQUE_NO = ? " +
                "AND FULL_CHEQUE_NO LIKE ? " +
                "ORDER BY FULL_CHEQUE_NO";

        String param = "%" + fullChequeKeyword + "%";

        List<Map<String, Object>> results = jdbcTemplate.query(sql, new Object[]{chequeNo, param}, (rs, rowNum) -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", rowNum + 1);
            map.put("name", rs.getString("FULL_CHEQUE_NO"));
            return map;
        });

        return ResponseEntity.ok(results);
    }
}