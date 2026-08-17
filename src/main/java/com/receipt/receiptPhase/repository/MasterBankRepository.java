package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.MasterBankModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MasterBankRepository {

    @Autowired
    @Qualifier("receiptJdbcTemplate")
    private JdbcTemplate jdbcTemplate;


    public int save(MasterBankModel bank) {
        String sql = "INSERT INTO master_banks (bank_id, office_code, bank_code, bank_name, bank_description, " +
                "is_valid, date_created, user_created, is_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '0')";

        return jdbcTemplate.update(sql,
                bank.getBankId(), bank.getOfficeCode(), bank.getBankCode(),
                bank.getBankName(), bank.getBankDescription(), bank.getIsValid(),
                bank.getDateCreated(), bank.getUserCreated());
    }

    public List<MasterBankModel> getAllBanksData() {
        String sql = "SELECT * FROM master_banks WHERE COALESCE(is_deleted, '0') != '1' ORDER BY date_created DESC";
        return jdbcTemplate.query(sql, new MasterBankRowMapper());
    }


    public MasterBankModel findById(String bankId) {
        String sql = "SELECT * FROM master_banks WHERE bank_id = ?";
        List<MasterBankModel> list = jdbcTemplate.query(sql, new Object[]{bankId}, new MasterBankRowMapper());
        return list.isEmpty() ? null : list.get(0);
    }


    public int update(MasterBankModel bank) {
        String sql = "UPDATE master_banks SET office_code = ?, bank_code = ?, bank_name = ?, bank_description = ?, " +
                "is_valid = ?, date_modified = ?, user_modified = ? " +
                "WHERE bank_id = ?";

        return jdbcTemplate.update(sql,
                bank.getOfficeCode(), bank.getBankCode(), bank.getBankName(), bank.getBankDescription(),
                bank.getIsValid(), bank.getDateModified(), bank.getUserModified(), bank.getBankId());
    }


    public int delete(String bankId, String userId, String currentDateTime) {
        String sql = "UPDATE master_banks SET " +
                "is_valid = '0', user_invalid = ?, date_invalid = ?, " +
                "is_deleted = '1', user_deleted = ?, date_deleted = ? " +
                "WHERE bank_id = ?";

        return jdbcTemplate.update(sql, userId, currentDateTime, userId, currentDateTime, bankId);
    }

    // RowMapper
    private static class MasterBankRowMapper implements RowMapper<MasterBankModel> {
        @Override
        public MasterBankModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            MasterBankModel bank = new MasterBankModel();
            bank.setBankId(rs.getString("bank_id"));
            bank.setOfficeCode(rs.getString("office_code"));
            bank.setBankCode(rs.getString("bank_code"));
            bank.setBankName(rs.getString("bank_name"));
            bank.setBankDescription(rs.getString("bank_description"));
            bank.setIsValid(rs.getString("is_valid"));
            bank.setDateCreated(rs.getString("date_created"));
            bank.setUserCreated(rs.getString("user_created"));
            bank.setDateModified(rs.getString("date_modified"));
            bank.setUserModified(rs.getString("user_modified"));
            bank.setDateInvalid(rs.getString("date_invalid"));
            bank.setUserInvalid(rs.getString("user_invalid"));
            bank.setIsDeleted(rs.getString("is_deleted"));
            bank.setDateDeleted(rs.getString("date_deleted"));
            bank.setUserDeleted(rs.getString("user_deleted"));
            return bank;
        }
    }
}