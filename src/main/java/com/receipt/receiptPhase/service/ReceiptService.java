package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Transactional
    public String saveReceipt(ReceiptModal receipt) {

        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String maxNo = receiptRepository.findMaxTransactionNoForDate(datePrefix + "%");

        int nextNo = 1;
        if (maxNo != null) {
            String suffix = maxNo.substring(maxNo.length() - 4);
            nextNo = Integer.parseInt(suffix) + 1;
        }
        String transactionNo = datePrefix + String.format("%04d", nextNo);


        receipt.setTransactionNo(transactionNo);
        receipt.setStatus(true); // Active


        receiptRepository.insertReceipt(
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
                receipt.getPostedToCoda() ? "1" : "0",
                receipt.getStatus() ? "1" : "0",
                receipt.getBank(),
                receipt.getCreatedDate(),
                receipt.getCreatedUser(),
                receipt.getModifiedDate(),
                receipt.getModifiedUser()
        );

        return transactionNo;
    }

    public List<ReceiptModal> getAllReceipts() {
        return receiptRepository.findActiveReceipts();
    }
}