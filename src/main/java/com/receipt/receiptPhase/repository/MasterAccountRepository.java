package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.MasterAccountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MasterAccountRepository {

    @Autowired
    @Qualifier("receiptJdbcTemplate") // உங்களின் முந்தைய code படி Qualifier சேர்த்துள்ளேன்
    private JdbcTemplate jdbcTemplate;


    public int save(MasterAccountModel account) {
        String sql = "INSERT INTO master_accounts (account_id, office_code, account_currency, account_payment_mode, " +
                "account_code, account_display_name, account_seq, is_valid, date_created, user_created, is_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '0')";

        return jdbcTemplate.update(sql,
                account.getAccountId(), account.getOfficeCode(), account.getAccountCurrency(),
                account.getAccountPaymentMode(), account.getAccountCode(), account.getAccountDisplayName(),
                account.getAccountSeq(), account.getIsValid(), account.getDateCreated(), account.getUserCreated());
    }


    public List<MasterAccountModel> findAll() {
        String sql = "SELECT * FROM master_accounts WHERE COALESCE(is_deleted, '0') != '1'";
        return jdbcTemplate.query(sql, new MasterAccountRowMapper());
    }


    public MasterAccountModel findById(String accountId) {
        String sql = "SELECT * FROM master_accounts WHERE account_id = ? AND COALESCE(is_deleted, '0') != '1'";
        List<MasterAccountModel> list = jdbcTemplate.query(sql, new Object[]{accountId}, new MasterAccountRowMapper());
        return list.isEmpty() ? null : list.get(0);
    }


    public int update(MasterAccountModel account) {
        String sql = "UPDATE master_accounts SET office_code = ?, account_currency = ?, account_payment_mode = ?, " +
                "account_code = ?, account_display_name = ?, account_seq = ?, is_valid = ?, date_modified = ?, user_modified = ? " +
                "WHERE account_id = ?";

        return jdbcTemplate.update(sql,
                account.getOfficeCode(), account.getAccountCurrency(), account.getAccountPaymentMode(),
                account.getAccountCode(), account.getAccountDisplayName(), account.getAccountSeq(),
                account.getIsValid(), account.getDateModified(), account.getUserModified(), account.getAccountId());
    }


    public int delete(String accountId, String userDeleted, String dateDeleted) {
        String sql = "UPDATE master_accounts SET is_deleted = '1', user_deleted = ?, date_deleted = ? WHERE account_id = ?";
        return jdbcTemplate.update(sql, userDeleted, dateDeleted, accountId);
    }


    private static class MasterAccountRowMapper implements RowMapper<MasterAccountModel> {
        @Override
        public MasterAccountModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            MasterAccountModel account = new MasterAccountModel();
            account.setAccountId(rs.getString("account_id"));
            account.setOfficeCode(rs.getString("office_code"));
            account.setAccountCurrency(rs.getString("account_currency"));
            account.setAccountPaymentMode(rs.getString("account_payment_mode"));
            account.setAccountCode(rs.getString("account_code"));
            account.setAccountDisplayName(rs.getString("account_display_name"));
            account.setAccountSeq(rs.getObject("account_seq") != null ? rs.getInt("account_seq") : null);
            account.setIsValid(rs.getString("is_valid"));
            account.setDateCreated(rs.getString("date_created"));
            account.setUserCreated(rs.getString("user_created"));
            account.setDateModified(rs.getString("date_modified"));
            account.setUserModified(rs.getString("user_modified"));
            account.setIsDeleted(rs.getString("is_deleted"));
            return account;
        }
    }
}