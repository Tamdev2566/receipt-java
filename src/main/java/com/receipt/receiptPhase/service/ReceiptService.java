package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.model.DashboardStats;
import com.receipt.receiptPhase.model.ReceiptModal;
import com.receipt.receiptPhase.repository.ReceiptRepository;
import com.receipt.receiptPhase.utils.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceiptService {
    private final ReceiptRepository repository;

    public ReceiptService(ReceiptRepository repository) {
        this.repository = repository;
    }

    public ReceiptModal createReceipt(ReceiptModal receipt) {
        receipt.setId(IdGenerator.generateId());
        receipt.setStatus("ACTIVE");
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setUpdatedAt(LocalDateTime.now());
        return repository.save(receipt);
    }

    public List<ReceiptModal> getAllReceipts() {
        return repository.findAll().stream()
                .filter(r -> !"REMOVED".equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    public ReceiptModal updateReceipt(String id, ReceiptModal updatedData) {
        ReceiptModal receipt = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));

        receipt.setTitle(updatedData.getTitle());
        receipt.setAmount(updatedData.getAmount());
        receipt.setUpdatedAt(LocalDateTime.now());
        return repository.save(receipt);
    }

    public ReceiptModal undoVerify(String id) {
        ReceiptModal receipt = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));

        receipt.setStatus("UNDO_VERIFIED");
        receipt.setUpdatedAt(LocalDateTime.now());
        return repository.save(receipt);
    }

    public ReceiptModal removeReceipt(String id) {
        ReceiptModal receipt = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));

        receipt.setStatus("REMOVED");
        receipt.setUpdatedAt(LocalDateTime.now());
        return repository.save(receipt);
    }

    public DashboardStats getDashboardStats() {
        List<ReceiptModal> all = repository.findAll();

        long total = all.size();
        long active = all.stream().filter(r -> "ACTIVE".equals(r.getStatus())).count();
        long undo = all.stream().filter(r -> "UNDO_VERIFIED".equals(r.getStatus())).count();
        long removed = all.stream().filter(r -> "REMOVED".equals(r.getStatus())).count();

        // Removed தவிர மீதமுள்ள ஆக்டிவ் தொகையின் கூட்டுத்தொகை
        double totalAmount = all.stream()
                .filter(r -> !"REMOVED".equals(r.getStatus()))
                .mapToDouble(ReceiptModal::getAmount)
                .sum();

        return new DashboardStats(total, active, undo, removed, totalAmount);
    }
}