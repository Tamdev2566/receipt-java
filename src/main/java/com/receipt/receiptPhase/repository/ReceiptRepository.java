package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.ReceiptModal;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<ReceiptModal, String> {
  @Modifying
  @Query(
    value = """
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
        :locationId,
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
    """,
    nativeQuery = true
  )
  int insertReceipt(
    @Param("transactionNo") String transactionNo,
    @Param("transactionDate") String transactionDate,
    @Param("locationId") String locationId,
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

  @Query(
    value = """
    SELECT *
    FROM receipt
    ORDER BY modified_date DESC, transaction_no DESC
    """,
    nativeQuery = true
  )
  List<ReceiptModal> findAllReceipts();

  @Query(
    value = """
    SELECT *
    FROM receipt
    WHERE office_code = :locationId
    ORDER BY modified_date DESC, transaction_no DESC
    """,
    nativeQuery = true
  )
  List<ReceiptModal> findAllReceiptsByLocation(
    @Param("locationId") String locationId
  );

  @Query(
    value = "SELECT MAX(transaction_no) FROM receipt WHERE transaction_no LIKE :datePrefix",
    nativeQuery = true
  )
  String findMaxTransactionNoForDate(@Param("datePrefix") String datePrefix);
}
