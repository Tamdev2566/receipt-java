package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.ReceiptModal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<ReceiptModal, String> {
    @Modifying
    @Query(value = """
            INSERT INTO receipt (
                transaction_no,
                transaction_date,
                office_code,
                payment_mode,
                receipt_date,
                reference_no,
                currency_code,
                amount,
                bank_charge,
                paid_invoice_total,
                receipt_total,
                balance_amount,
                posted_to_coda,
                status,
                bank,
                created_date,
                created_user,
                modified_date,
                modified_user
            )
            VALUES (
                :transactionNo,
                :transactionDate,
                :officeCode,
                :paymentMode,
                :receiptDate,
                :referenceNo,
                :currencyCode,
                :amount,
                :bankCharge,
                :paidInvoiceTotal,
                :receiptTotal,
                :balanceAmount,
                CAST(:postedToCoda AS bit(1)),
                CAST(:status AS bit(1)),
                :bank,
                :createdDate,
                :createdUser,
                :modifiedDate,
                :modifiedUser
            )
            """, nativeQuery = true)
    int insertReceipt(@Param("transactionNo") String transactionNo,
                      @Param("transactionDate") String transactionDate,
                      @Param("officeCode") String officeCode,
                      @Param("paymentMode") String paymentMode,
                      @Param("receiptDate") String receiptDate,
                      @Param("referenceNo") String referenceNo,
                      @Param("currencyCode") String currencyCode,
                      @Param("amount") BigDecimal amount,
                      @Param("bankCharge") BigDecimal bankCharge,
                      @Param("paidInvoiceTotal") BigDecimal paidInvoiceTotal,
                      @Param("receiptTotal") BigDecimal receiptTotal,
                      @Param("balanceAmount") BigDecimal balanceAmount,
                      @Param("postedToCoda") String postedToCoda,
                      @Param("status") String status,
                      @Param("bank") String bank,
                      @Param("createdDate") String createdDate,
                      @Param("createdUser") String createdUser,
                      @Param("modifiedDate") String modifiedDate,
                      @Param("modifiedUser") String modifiedUser);

    @Query(value = """
            SELECT *
            FROM receipt
            WHERE status = B'0'
            ORDER BY modified_date DESC, transaction_no DESC
            """, nativeQuery = true)
    List<ReceiptModal> findActiveReceipts();

    @Query(value = """
            SELECT COALESCE(MAX(CAST(SUBSTRING(transaction_no FROM 4) AS INTEGER)), 0)
            FROM receipt
            WHERE transaction_no ~ '^RCT[0-9]+$'
            """, nativeQuery = true)
    int findLatestReceiptNumber();

    @Query(value = """
            SELECT *
            FROM receipt
            WHERE transaction_no = :transactionNo
            """, nativeQuery = true)
    Optional<ReceiptModal> findByTransactionNo(@Param("transactionNo") String transactionNo);

    @Modifying
    @Query(value = """
            UPDATE receipt
            SET amount = :amount,
                bank = :bank,
                payment_mode = :paymentMode,
                modified_date = :modifiedDate
            WHERE transaction_no = :transactionNo
            """, nativeQuery = true)
    int updateReceiptFields(@Param("transactionNo") String transactionNo,
                            @Param("amount") BigDecimal amount,
                            @Param("bank") String bank,
                            @Param("paymentMode") String paymentMode,
                            @Param("modifiedDate") String modifiedDate);

    @Modifying
    @Query(value = """
            UPDATE receipt
            SET status = B'0',
                modified_date = :modifiedDate
            WHERE transaction_no = :transactionNo
            """, nativeQuery = true)
    int markReceiptInactive(@Param("transactionNo") String transactionNo,
                            @Param("modifiedDate") String modifiedDate);

    @Query(value = """
            SELECT COUNT(*) AS "totalReceipts",
                   COUNT(*) FILTER (WHERE status = B'1') AS "activeReceipts",
                   COUNT(*) FILTER (WHERE status = B'0' OR status IS NULL) AS "inactiveReceipts",
                   COALESCE(SUM(CASE WHEN status = B'1' THEN amount ELSE 0 END), 0) AS "totalAmount"
            FROM receipt
            """, nativeQuery = true)
    DashboardStatsProjection getDashboardStats();

    @Query(value = "SELECT MAX(transaction_no) FROM receipt WHERE transaction_no LIKE :datePrefix", nativeQuery = true)
    String findMaxTransactionNoForDate(@Param("datePrefix") String datePrefix);

    interface DashboardStatsProjection {
        long getTotalReceipts();
        long getActiveReceipts();
        long getInactiveReceipts();
        BigDecimal getTotalAmount();
    }
}
