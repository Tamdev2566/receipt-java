package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.service.LegacyReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LegacyReceiptController {
    private final LegacyReceiptService service;

    public LegacyReceiptController(LegacyReceiptService service) {
        this.service = service;
    }

    @GetMapping("/master/banks")
    public ResponseEntity<List<Map<String, Object>>> getBanks() {
        return ResponseEntity.ok(service.getBanks());
    }

    @GetMapping("/master/accounts")
    public ResponseEntity<List<Map<String, Object>>> getAccounts(@RequestParam(required = false) String currencyCode,
                                                                 @RequestParam(required = false) String paymentMode) {
        return ResponseEntity.ok(service.getAccounts(currencyCode, paymentMode));
    }

    @GetMapping("/receipts/search")
    public ResponseEntity<List<Map<String, Object>>> getReceipts(@RequestParam(required = false) String transactionNo,
                                                                 @RequestParam(required = false) String referenceNo,
                                                                 @RequestParam(required = false) String paymentMode,
                                                                 @RequestParam(required = false) String fromDate,
                                                                 @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(service.getReceipts(transactionNo, referenceNo, paymentMode, fromDate, toDate));
    }

    @GetMapping("/receipts/reference-options")
    public ResponseEntity<List<Map<String, Object>>> getReceiptReferenceOptions(@RequestParam(required = false) String paymentMode) {
        return ResponseEntity.ok(service.getReceiptReferenceOptions(paymentMode));
    }

    @PatchMapping("/receipts/reference")
    public ResponseEntity<Map<String, Object>> updateReceiptReference(@RequestBody Map<String, Object> request) {
        int updated = service.updateReceiptReference(request);
        return ResponseEntity.ok(Map.of(
                "message", updated > 0 ? "Reference number updated successfully" : "No matching receipt found",
                "updatedRows", updated
        ));
    }

    @GetMapping("/cheque-reader")
    public ResponseEntity<List<Map<String, Object>>> getChequeReader(@RequestParam(required = false) String chequeNo,
                                                                     @RequestParam(required = false) String fullChequeNo) {
        return ResponseEntity.ok(service.getChequeReader(chequeNo, fullChequeNo));
    }

    @PostMapping("/cheque-reader")
    public ResponseEntity<Map<String, Object>> createChequeReader(@RequestBody Map<String, Object> request) {
        int inserted = service.createChequeReader(request);
        return ResponseEntity.ok(Map.of(
                "message", "Cheque reader record created successfully",
                "insertedRows", inserted
        ));
    }

    @PatchMapping("/cheque-reader/cancel")
    public ResponseEntity<Map<String, Object>> cancelCheque(@RequestBody Map<String, Object> request) {
        int updated = service.cancelCheque(request);
        return ResponseEntity.ok(Map.of(
                "message", updated > 0 ? "Cheque cancelled successfully" : "No matching cheque found",
                "updatedRows", updated
        ));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<Map<String, Object>>> getInvoices(@RequestParam(required = false) String customerName,
                                                                 @RequestParam(required = false) String vesselName,
                                                                 @RequestParam(required = false) String voyageNo,
                                                                 @RequestParam(required = false) String referenceNo) {
        return ResponseEntity.ok(service.getInvoices(customerName, vesselName, voyageNo, referenceNo));
    }

    @GetMapping("/invoices/outstanding")
    public ResponseEntity<List<Map<String, Object>>> getOutstandingInvoices(@RequestParam(required = false) String customerName,
                                                                           @RequestParam(required = false) String vesselName,
                                                                           @RequestParam(required = false) String voyageNo,
                                                                           @RequestParam(required = false) String referenceNo) {
        return ResponseEntity.ok(service.getOutstandingInvoices(customerName, vesselName, voyageNo, referenceNo));
    }

    @PatchMapping("/invoices/remove")
    public ResponseEntity<Map<String, Object>> removeInvoice(@RequestBody Map<String, Object> request) {
        int updated = service.removeInvoice(request);
        return ResponseEntity.ok(Map.of(
                "message", "Invoice removal processed",
                "updatedRows", updated
        ));
    }

    @GetMapping("/reports/cheques/daily-scan")
    public ResponseEntity<List<Map<String, Object>>> getDailyScanReport(@RequestParam(required = false) String fromDate,
                                                                        @RequestParam(required = false) String toDate,
                                                                        @RequestParam(required = false) String bound) {
        return ResponseEntity.ok(service.getDailyScanReport(fromDate, toDate, bound));
    }

    @GetMapping("/reports/cheques/aging")
    public ResponseEntity<List<Map<String, Object>>> getAgingChequeReport(@RequestParam(required = false) Integer days) {
        return ResponseEntity.ok(service.getAgingChequeReport(days));
    }

    @GetMapping("/reports/cheques/cancelled")
    public ResponseEntity<List<Map<String, Object>>> getCancelledChequeReport(@RequestParam(required = false) String fromDate,
                                                                             @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(service.getCancelledChequeReport(fromDate, toDate));
    }

    @GetMapping("/reports/invoices/removed")
    public ResponseEntity<List<Map<String, Object>>> getRemovedInvoiceReport(@RequestParam(required = false) String fromDate,
                                                                            @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(service.getRemovedInvoiceReport(fromDate, toDate));
    }

    @GetMapping("/reports/references/updated")
    public ResponseEntity<List<Map<String, Object>>> getUpdatedReferenceReport(@RequestParam(required = false) String paymentMode,
                                                                              @RequestParam(required = false) String fromDate,
                                                                              @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(service.getUpdatedReferenceReport(paymentMode, fromDate, toDate));
    }
}
