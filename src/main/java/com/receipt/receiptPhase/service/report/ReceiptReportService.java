package com.receipt.receiptPhase.service.report;

import com.receipt.receiptPhase.dto.report.ReceiptReport;
import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.repository.ReceiptReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Service
public class ReceiptReportService {

    @Autowired
    private ReceiptReportRepository receiptRepository;

    public List<ReceiptModal> getFilteredReceipts(ReceiptReport filterDTO) {

        String formattedDate = filterDTO.getTransactionDate();
        if (formattedDate != null && !formattedDate.trim().isEmpty()) {
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(formattedDate.trim(), inputFormatter);
                formattedDate = date.format(dbFormatter);
            } catch (Exception e) {

                formattedDate = filterDTO.getTransactionDate();
            }
        }


        String formattedPaymentMode = filterDTO.getPaymentMode();
        if (formattedPaymentMode != null) {
            formattedPaymentMode = formattedPaymentMode.replace("/", "").trim();
        }

        return receiptRepository.filterReceipts(
                formattedDate,
                formattedPaymentMode,
                filterDTO.getCurrencyCode(),
                filterDTO.getReportFor()
        );
    }
}