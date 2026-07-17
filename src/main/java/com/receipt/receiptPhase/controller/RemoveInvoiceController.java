package com.receipt.receiptPhase.controller;

import com.receipt.receiptPhase.service.RemoveInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/removeInvoices")
public class RemoveInvoiceController {

    @Autowired
    private RemoveInvoiceService invoiceService;


    @GetMapping("/search")
    public ResponseEntity<?> searchInvoices(@RequestParam String customer,
                                            @RequestParam String vessel,
                                            @RequestParam String voyage) {
        return ResponseEntity.ok(invoiceService.getInvoices(customer, vessel, voyage));
    }


    @PostMapping("/remove")
    public ResponseEntity<String> removeInvoices(@RequestBody Map<String, Object> request) {
        List<String> referenceNos = (List<String>) request.get("referenceNos");
        String userId = (String) request.get("userId");
        String remark = (String) request.get("remark");

        if (referenceNos == null || referenceNos.isEmpty()) {
            return ResponseEntity.badRequest().body("No invoices selected.");
        }

        invoiceService.removeInvoices(referenceNos, userId, remark);
        return ResponseEntity.ok("Successfully removed invoices.");
    }
}