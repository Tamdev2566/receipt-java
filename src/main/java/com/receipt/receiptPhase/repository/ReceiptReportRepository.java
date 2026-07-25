package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.ReceiptModal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptReportRepository extends JpaRepository<ReceiptModal, String> {

    @Query("SELECT r FROM ReceiptModal r WHERE " +
            "(:transactionDate IS NULL OR TRIM(:transactionDate) = '' OR r.transactionDate LIKE CONCAT('%', :transactionDate, '%')) AND " +
            "(:paymentMode IS NULL OR TRIM(:paymentMode) = '' OR UPPER(r.paymentMode) = UPPER(:paymentMode)) AND " +
            "(:currencyCode IS NULL OR TRIM(:currencyCode) = '' OR UPPER(r.currencyCode) = UPPER(:currencyCode)) AND " +
            "(:createdUser IS NULL OR TRIM(:createdUser) = '' OR UPPER(r.createdUser) = UPPER(:createdUser))")
    List<ReceiptModal> filterReceipts(
            @Param("transactionDate") String transactionDate,
            @Param("paymentMode") String paymentMode,
            @Param("currencyCode") String currencyCode,
            @Param("createdUser") String createdUser
    );
}