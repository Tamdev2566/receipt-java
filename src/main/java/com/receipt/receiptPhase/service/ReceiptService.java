package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.DashboardStats;
import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.repository.ReceiptRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class ReceiptService {
    private final ReceiptRepository repository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReceiptService(ReceiptRepository repository) {
        this.repository = repository;
    }

    public ReceiptModal createReceipt(ReceiptModal receipt) {
        if (receipt.getTransactionNo() == null || receipt.getTransactionNo().isEmpty()) {
            receipt.setTransactionNo(generateNextTransactionNo());
        }
        receipt.setStatus(true); // true means ACTIVE
        receipt.setCreatedDate(LocalDateTime.now().format(formatter));
        receipt.setModifiedDate(LocalDateTime.now().format(formatter));

        repository.insertReceipt(
                receipt.getTransactionNo(),
                receipt.getTransactionDate(),
                receipt.getOfficeCode(),
                receipt.getPaymentMode(),
                receipt.getReceiptDate(),
                receipt.getReferenceNo(),
                receipt.getCurrencyCode(),
                receipt.getAmount(),
                receipt.getBankCharge(),
                receipt.getPaidInvoiceTotal(),
                receipt.getReceiptTotal(),
                receipt.getBalanceAmount(),
                toBitValue(receipt.getPostedToCoda()),
                toBitValue(receipt.getStatus()),
                receipt.getBank(),
                receipt.getCreatedDate(),
                receipt.getCreatedUser(),
                receipt.getModifiedDate(),
                receipt.getModifiedUser()
        );
        return getReceiptByTransactionNo(receipt.getTransactionNo());
    }

    public List<ReceiptModal> getAllReceipts() {
        return repository.findActiveReceipts();
    }

    public ReceiptModal updateReceipt(String id, ReceiptModal updatedData) {
        String modifiedDate = LocalDateTime.now().format(formatter);
        int updatedRows = repository.updateReceiptFields(
                id,
                updatedData.getAmount(),
                updatedData.getBank(),
                updatedData.getPaymentMode(),
                modifiedDate
        );
        if (updatedRows == 0) {
            throw new RuntimeException("Receipt not found with id: " + id);
        }
        return getReceiptByTransactionNo(id);
    }

    public ReceiptModal undoVerify(String id) {
        markInactive(id);
        return getReceiptByTransactionNo(id);
    }

    public ReceiptModal removeReceipt(String id) {
        markInactive(id);
        return getReceiptByTransactionNo(id);
    }

    public DashboardStats getDashboardStats() {
        ReceiptRepository.DashboardStatsProjection stats = repository.getDashboardStats();

        return new DashboardStats(
                stats.getTotalReceipts(),
                stats.getActiveReceipts(),
                stats.getInactiveReceipts(),
                0,
                stats.getTotalAmount().doubleValue()
        );
    }

    private void markInactive(String id) {
        int updatedRows = repository.markReceiptInactive(id, LocalDateTime.now().format(formatter));
        if (updatedRows == 0) {
            throw new RuntimeException("Receipt not found with id: " + id);
        }
    }

    private ReceiptModal getReceiptByTransactionNo(String id) {
        return repository.findByTransactionNo(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));
    }

    private String generateNextTransactionNo() {

        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        String maxTransNo = repository.findMaxTransactionNoForDate(datePrefix + "%");


        if (maxTransNo == null || maxTransNo.isEmpty()) {

            return datePrefix + "0001";
        } else {

            String sequenceStr = maxTransNo.substring(maxTransNo.length() - 4);
            int nextSequence = Integer.parseInt(sequenceStr) + 1;

            return datePrefix + String.format("%04d", nextSequence);
        }
    }

    private String toBitValue(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? "1" : "0";
    }
}
