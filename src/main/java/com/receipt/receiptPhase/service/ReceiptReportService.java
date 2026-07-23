package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.dto.ReceiptReport;
import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.repository.ReceiptReportRepository;
import com.receipt.receiptPhase.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptReportService {

    @Autowired
    private ReceiptReportRepository receiptRepository;

    public List<ReceiptModal> getFilteredReceipts(ReceiptReport filterDTO) {
        return receiptRepository.filterReceipts(
                filterDTO.getTransactionDate(),
                filterDTO.getPaymentMode(),
                filterDTO.getCurrencyCode(),
                filterDTO.getReportFor()
        );
    }
}