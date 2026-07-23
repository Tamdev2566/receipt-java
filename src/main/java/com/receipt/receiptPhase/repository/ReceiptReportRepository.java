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
            "(:transactionDate IS NULL OR :transactionDate = '' OR r.transactionDate = :transactionDate) AND " +
            "(:paymentMode IS NULL OR :paymentMode = '' OR r.paymentMode = :paymentMode) AND " +
            "(:currencyCode IS NULL OR :currencyCode = '' OR r.currencyCode = :currencyCode) AND " +
            "(:createdUser IS NULL OR :createdUser = '' OR r.createdUser = :createdUser)")
    List<ReceiptModal> filterReceipts(
            @Param("transactionDate") String transactionDate,
            @Param("paymentMode") String paymentMode,
            @Param("currencyCode") String currencyCode,
            @Param("createdUser") String createdUser
    );
}