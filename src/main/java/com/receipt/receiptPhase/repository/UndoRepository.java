package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.ReceiptModal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface UndoRepository extends JpaRepository<ReceiptModal, String> {

    @Query(value = "SELECT DISTINCT r.Transaction_No, r.Transaction_Date, " +
            "r.currency_code as Currency, r.Amount, " +
            "r.Paid_Invoice_Total, r.Receipt_Date, r.Reference_No " +
            "FROM Receipt r INNER JOIN Invoice i ON i.Transaction_No = r.Transaction_No " +
            "WHERE (:invNo IS NULL OR i.Transaction_No = :invNo) " +
            "AND (:chequeNo IS NULL OR r.Reference_No = :chequeNo) " +
            "AND (:blNo IS NULL OR i.BL_No = :blNo) " +

            "AND (r.Posted_to_coda IS NULL OR r.Posted_to_coda = '0') " +
            "AND (r.Status IS NULL OR r.Status = '0') ORDER BY r.Transaction_No", nativeQuery = true)
    List<Map<String, Object>> retrieveReceipts(@Param("invNo") String invNo,
                                               @Param("chequeNo") String chequeNo,
                                               @Param("blNo") String blNo);

    @Query(value = "SELECT DISTINCT i2.Transaction_No, i2.BL_No, i2.Vessel_Name, i2.Voyage_No, i2.Customer_Name, " +
            "i2.Type, i2.Reference_Date, i2.Reference_No, i2.Currency, i2.Settlement_Amt, " +
            "i2.value_doc as SGD_Amount, i2.value_dual as USD_Amount " +
            "FROM Invoice i1 INNER JOIN Invoice i2 ON i1.Transaction_No = i2.Transaction_No " +
            "INNER JOIN Receipt r ON i1.Transaction_No = r.Transaction_No " +
            "WHERE (:invNo IS NULL OR i1.Transaction_No = :invNo) " +
            "AND (:chequeNo IS NULL OR r.Reference_No = :chequeNo) " +
            "AND (:blNo IS NULL OR i1.BL_No = :blNo) " +
            // Fixed: 0 mapped to '0'
            "AND (r.Posted_to_coda IS NULL OR r.Posted_to_coda = '0') " +
            "AND (r.Status IS NULL OR r.Status = '0') ORDER BY i2.Transaction_No", nativeQuery = true)
    List<Map<String, Object>> retrieveInvoices(@Param("invNo") String invNo,
                                               @Param("chequeNo") String chequeNo,
                                               @Param("blNo") String blNo);

    @Query(value = "SELECT p.* FROM partial p INNER JOIN receipt r ON p.transaction_no = r.transaction_no " +
            "WHERE (r.status IS NULL OR r.status = '0') " +
            "AND p.reference_no = :refNo ORDER BY p.transaction_date", nativeQuery = true)

    List<Map<String, Object>> getPartialDetails(@Param("refNo") String refNo);

    @Modifying

    @Query(value = "UPDATE Receipt SET Status = '1', modified_date = :modDate WHERE Transaction_No = :transNo", nativeQuery = true)
    void softDeleteReceipt(@Param("transNo") String transNo, @Param("modDate") LocalDateTime modDate);
}