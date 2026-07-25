package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.ReceiptModal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptReportRepository extends JpaRepository<ReceiptModal, String> {

    @Query(value = "SELECT * FROM receipt r WHERE " +
            "(:transactionDate IS NULL OR :transactionDate = '' OR r.transaction_date LIKE CONCAT(:transactionDate, '%')) AND " +
            "(:paymentMode IS NULL OR :paymentMode = '' OR UPPER(TRIM(r.payment_mode)) = UPPER(TRIM(:paymentMode))) AND " +
            "(:currencyCode IS NULL OR :currencyCode = '' OR UPPER(TRIM(r.currency_code)) = UPPER(TRIM(:currencyCode))) AND " +
            "(:createdUser IS NULL OR :createdUser = '' OR UPPER(TRIM(r.created_user)) = UPPER(TRIM(:createdUser)))",
            nativeQuery = true)
    List<ReceiptModal> filterReceipts(
            @Param("transactionDate") String transactionDate,
            @Param("paymentMode") String paymentMode,
            @Param("currencyCode") String currencyCode,
            @Param("createdUser") String createdUser
    );
}