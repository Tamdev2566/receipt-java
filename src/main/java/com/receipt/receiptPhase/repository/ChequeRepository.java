package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.ChequeReaderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Repository
public class ChequeRepository {

    private final JdbcTemplate receiptJdbcTemplate;
    private final JdbcTemplate userAuthJdbcTemplate;

    @Autowired
    public ChequeRepository(
            @Qualifier("receiptJdbcTemplate") JdbcTemplate receiptJdbcTemplate,
            @Qualifier("userAuthJdbcTemplate") JdbcTemplate userAuthJdbcTemplate) {
        this.receiptJdbcTemplate = receiptJdbcTemplate;
        this.userAuthJdbcTemplate = userAuthJdbcTemplate;
    }


    public String getUserGroup(String uid) {
        String sql = "SELECT office_id FROM users WHERE user_id = ?";
        List<Map<String, Object>> rows = userAuthJdbcTemplate.queryForList(sql, uid);
        if (!rows.isEmpty()) {
            return (String) rows.get(0).get("office_id");
        }
        return null;
    }


    public String getBankNameByCode(String bankCode) {
        String sql = "SELECT bank_name FROM master_banks WHERE bank_code = ? AND is_valid = '1'";
        List<Map<String, Object>> rows = receiptJdbcTemplate.queryForList(sql, bankCode);
        if (!rows.isEmpty()) {
            return (String) rows.get(0).get("bank_name");
        }
        return null;
    }

    public boolean checkBankExistsByName(String bankName) {
        String sql = "SELECT COUNT(*) FROM master_banks WHERE bank_name = ? AND is_valid = '1'";
        Integer count = receiptJdbcTemplate.queryForObject(sql, Integer.class, bankName);
        return count != null && count > 0;
    }

    public boolean checkDuplicateCheque(String chequeNo, String fullChequeNo) {
        String sql = "SELECT COUNT(*) FROM cheque_reader WHERE EXTRACT(YEAR FROM AGE(CURRENT_DATE, date_created::timestamp)) <= 5 " +
                "AND COALESCE(is_valid, '1') != '0' AND cheque_no = ? AND full_cheque_no = ?";
        Integer count = receiptJdbcTemplate.queryForObject(sql, Integer.class, chequeNo, fullChequeNo);
        return count != null && count > 0;
    }

    public void saveCheque(ChequeReaderModel cheque, String locationId) {
        String sql = "INSERT INTO cheque_reader (cheque_reader_id, office_code, bound, cheque_no, bank_name, full_cheque_no, scan_user_id, date_created, auto_read, is_valid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String chequeReaderId = "CR" + String.format("%04d", Math.abs(System.currentTimeMillis() % 10000));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = cheque.getCreateTime().format(formatter);

        receiptJdbcTemplate.update(sql,
                chequeReaderId,
                locationId,
                cheque.getBound(),
                cheque.getChequeNo(),
                cheque.getBankName(),
                cheque.getFullChequeNo(),
                cheque.getScanUserId(),
                formattedDate,
                cheque.isAutoRead() ? "1" : "0",
                "1"
        );
    }

    public int updateCheque(ChequeReaderModel cheque) {
        StringBuilder sql = new StringBuilder("UPDATE cheque_reader SET ");
        List<Object> params = new java.util.ArrayList<>();
        if (cheque.getBankName() != null && !cheque.getBankName().trim().isEmpty()) {
            sql.append("bank_name = ?, ");
            params.add(cheque.getBankName().trim());
        }
        if (cheque.getChequeNo() != null && !cheque.getChequeNo().trim().isEmpty()) {
            sql.append("cheque_no = ?, ");
            params.add(cheque.getChequeNo().trim());
        }
        if (cheque.getFullChequeNo() != null && !cheque.getFullChequeNo().trim().isEmpty()) {
            sql.append("full_cheque_no = ?, ");
            params.add(cheque.getFullChequeNo().trim());
        }
        if (cheque.getBound() != null && !cheque.getBound().trim().isEmpty()) {
            sql.append("bound = ?, ");
            params.add(cheque.getBound().trim());
        }
        if (cheque.getIsValid() != null && !cheque.getIsValid().trim().isEmpty()) {
            sql.append("is_valid = ?, ");
            params.add(cheque.getIsValid().trim());
        }


        if (params.isEmpty()) {
            return 0;
        }


        sql.setLength(sql.length() - 2);
        sql.append(" WHERE cheque_reader_id = ?");
        params.add(cheque.getId().trim());

        return receiptJdbcTemplate.update(sql.toString(), params.toArray());
    }


    public List<ChequeReaderModel> getAllCheques(String locationId) {
        String sql = "SELECT cheque_reader_id, cheque_no, bank_name, full_cheque_no, bound, scan_user_id, auto_read, date_created, is_valid " +
                "FROM cheque_reader WHERE office_code = ? AND COALESCE(is_valid, '1') != '0' ORDER BY date_created DESC";
        return receiptJdbcTemplate.query(sql, new ChequeRowMapper(), locationId);
    }


    public List<ChequeReaderModel> getChequesByUserId(String uid, String locationId) {
        String sql = "SELECT cheque_reader_id, cheque_no, bank_name, full_cheque_no, bound, scan_user_id, auto_read, date_created, is_valid " +
                "FROM cheque_reader WHERE scan_user_id = ? AND office_code = ? AND COALESCE(is_valid, '1') != '0' ORDER BY date_created DESC";
        return receiptJdbcTemplate.query(sql, new Object[]{uid, locationId}, new ChequeRowMapper());
    }

    private class ChequeRowMapper implements org.springframework.jdbc.core.RowMapper<ChequeReaderModel> {
        @Override
        public ChequeReaderModel mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            ChequeReaderModel cheque = new ChequeReaderModel();

            cheque.setId(rs.getString("cheque_reader_id"));
            cheque.setChequeNo(rs.getString("cheque_no"));
            cheque.setBankName(rs.getString("bank_name"));
            cheque.setFullChequeNo(rs.getString("full_cheque_no"));
            cheque.setBound(rs.getString("bound"));
            cheque.setScanUserId(rs.getString("scan_user_id"));
            cheque.setIsValid(rs.getString("is_valid"));
            String autoReadStr = rs.getString("auto_read");
            cheque.setAutoRead("1".equals(autoReadStr));

            java.sql.Timestamp dateCreated = rs.getTimestamp("date_created");
            if (dateCreated != null) {
                cheque.setCreateTime(dateCreated.toLocalDateTime());
                cheque.setLastModified(dateCreated.toLocalDateTime());
            }

            return cheque;
        }
    }
}
