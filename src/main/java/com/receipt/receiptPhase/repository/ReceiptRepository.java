package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.ReceiptModal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<ReceiptModal, Long> {

    Optional<ReceiptModal> findByTransactionNo(String transactionNo);

    @Query(value = """
            SELECT MAX(transaction_no)
            FROM receipt
            WHERE transaction_no LIKE CONCAT(:datePrefix, '%')
            """, nativeQuery = true)
    String findMaxTransactionNo(@Param("datePrefix") String datePrefix);

    @Query(value = "SELECT * FROM receipt WHERE status='1'", nativeQuery = true)
    List<ReceiptModal> findActiveReceipts();

    @Query(value = """
            INSERT INTO receipt(
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
            VALUES(
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
                :postedToCoda,
                :status,
                :bank,
                :createdDate,
                :createdUser,
                :modifiedDate,
                :modifiedUser
            )
            """, nativeQuery = true)
    @org.springframework.data.jpa.repository.Modifying
    void insertReceipt(
            @Param("transactionNo") String transactionNo,
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
            @Param("modifiedUser") String modifiedUser
    );

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = """
            UPDATE receipt
            SET amount=:amount,
                bank=:bank,
                payment_mode=:paymentMode,
                modified_date=:modifiedDate
            WHERE transaction_no=:transactionNo
            """, nativeQuery = true)
    int updateReceiptFields(
            @Param("transactionNo") String transactionNo,
            @Param("amount") BigDecimal amount,
            @Param("bank") String bank,
            @Param("paymentMode") String paymentMode,
            @Param("modifiedDate") String modifiedDate
    );

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = """
            UPDATE receipt
            SET status='0',
                modified_date=:modifiedDate
            WHERE transaction_no=:transactionNo
            """, nativeQuery = true)
    int markReceiptInactive(
            @Param("transactionNo") String transactionNo,
            @Param("modifiedDate") String modifiedDate
    );

    interface DashboardStatsProjection {
        Long getTotalReceipts();
        Long getActiveReceipts();
        Long getInactiveReceipts();
        BigDecimal getTotalAmount();
    }

    @Query(value = """
            SELECT
                COUNT(*) totalReceipts,
                COUNT(CASE WHEN status='1' THEN 1 END) activeReceipts,
                COUNT(CASE WHEN status='0' THEN 1 END) inactiveReceipts,
                COALESCE(SUM(amount),0) totalAmount
            FROM receipt
            """, nativeQuery = true)
    DashboardStatsProjection getDashboardStats();
}