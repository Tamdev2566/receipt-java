package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.ChequeReaderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

    public void saveCheque(ChequeReaderModel cheque) {
        String sql = "INSERT INTO cheque_reader (cheque_reader_id, office_code, bound, cheque_no, bank_name, full_cheque_no, scan_user_id, date_created, auto_read, is_valid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String chequeReaderId = "CR" + String.format("%04d", Math.abs(System.currentTimeMillis() % 10000));

        String officeCode = "OFF01";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = cheque.getCreateTime().format(formatter);

        receiptJdbcTemplate.update(sql,
                chequeReaderId,
                officeCode,
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
}