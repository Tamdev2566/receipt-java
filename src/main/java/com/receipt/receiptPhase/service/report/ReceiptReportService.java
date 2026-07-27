package com.receipt.receiptPhase.service.report;

import com.receipt.receiptPhase.dto.report.ReceiptReport;
import com.receipt.receiptPhase.repository.ReceiptReportRepository;
import com.receipt.receiptPhase.repository.ReceiptReportRepository.ReceiptWithCustomerProjection;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ReceiptReportService {

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ReceiptReportRepository receiptRepository;

    public ReceiptReportService(ReceiptReportRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    public List<ReceiptWithCustomerProjection> getFilteredReceipts(ReceiptReport filterDTO) {
        if (filterDTO == null) {
            return List.of();
        }

        String formattedDate = parseAndFormatDate(filterDTO.getTransactionDate());
        String formattedPaymentMode = cleanPaymentMode(filterDTO.getPaymentMode());

        return receiptRepository.filterReceiptsWithCustomer(
                formattedDate,
                formattedPaymentMode,
                filterDTO.getCurrencyCode(),
                filterDTO.getReportFor()
        );
    }

    private String parseAndFormatDate(String rawDate) {
        if (!StringUtils.hasText(rawDate)) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(rawDate.trim(), INPUT_FORMATTER);
            return date.format(DB_FORMATTER);
        } catch (DateTimeParseException e) {
            return rawDate.trim();
        }
    }

    private String cleanPaymentMode(String paymentMode) {
        if (!StringUtils.hasText(paymentMode)) {
            return null;
        }
        return paymentMode.replace("/", "").trim();
    }
}