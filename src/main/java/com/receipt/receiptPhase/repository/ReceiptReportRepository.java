package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.ReceiptModal;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptReportRepository
  extends JpaRepository<ReceiptModal, String>
{
  public interface ReceiptWithCustomerProjection {
    String getTransactionNo();
    String getTransactionDate();
    String getOfficeCode();
    String getPaymentMode();
    String getReceiptDate();
    String getReferenceNo();
    String getCurrencyCode();
    BigDecimal getAmount();
    BigDecimal getBankCharge();
    BigDecimal getPaidInvoiceTotal();
    BigDecimal getReceiptTotal();
    BigDecimal getBalanceAmount();
    Boolean getPostedToCoda();
    Boolean getStatus();
    String getBank();
    String getCreatedDate();
    String getCreatedUser();
    String getModifiedDate();
    String getModifiedUser();
    String getCustomer();
  }

  @Query(
    value = "SELECT " +
      "r.transaction_no AS transactionNo, " +
      "r.transaction_date AS transactionDate, " +
      "r.office_code AS officeCode, " +
      "r.payment_mode AS paymentMode, " +
      "r.receipt_date AS receiptDate, " +
      "r.reference_no AS referenceNo, " +
      "r.currency_code AS currencyCode, " +
      "r.amount AS amount, " +
      "r.bank_charge AS bankCharge, " +
      "r.paid_invoice_total AS paidInvoiceTotal, " +
      "r.receipt_total AS receiptTotal, " +
      "r.balance_amount AS balanceAmount, " +
      "r.posted_to_coda AS postedToCoda, " +
      "r.status AS status, " +
      "r.bank AS bank, " +
      "r.created_date AS createdDate, " +
      "r.created_user AS createdUser, " +
      "r.modified_date AS modifiedDate, " +
      "r.modified_user AS modifiedUser, " +
      "i.customer_name AS customer " +
      "FROM receipt r " +
      "LEFT JOIN invoice i ON r.transaction_no = i.transaction_no " +
      "WHERE r.status = '0' AND " +
      "(:transactionDate IS NULL OR CAST(:transactionDate AS VARCHAR) = '' OR r.transaction_date LIKE CONCAT(CAST(:transactionDate AS VARCHAR), '%')) AND " +
      "(:paymentMode IS NULL OR CAST(:paymentMode AS VARCHAR) = '' OR UPPER(TRIM(r.payment_mode)) = UPPER(TRIM(CAST(:paymentMode AS VARCHAR)))) AND " +
      "(:currencyCode IS NULL OR CAST(:currencyCode AS VARCHAR) = '' OR UPPER(TRIM(r.currency_code)) = UPPER(TRIM(CAST(:currencyCode AS VARCHAR)))) AND " +
      "(:createdUser IS NULL OR CAST(:createdUser AS VARCHAR) = '' OR UPPER(TRIM(r.created_user)) = UPPER(TRIM(CAST(:createdUser AS VARCHAR))))",
    nativeQuery = true
  )
  List<ReceiptWithCustomerProjection> filterReceiptsWithCustomer(
    @Param("transactionDate") String transactionDate,
    @Param("paymentMode") String paymentMode,
    @Param("currencyCode") String currencyCode,
    @Param("createdUser") String createdUser
  );
}
