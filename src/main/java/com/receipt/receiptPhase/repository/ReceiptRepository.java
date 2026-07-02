package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.ReceiptModal;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ReceiptRepository {
    private final Map<String, ReceiptModal> storage = new ConcurrentHashMap<>();

    public ReceiptModal save(ReceiptModal receipt) {
        storage.put(receipt.getId(), receipt);
        return receipt;
    }

    public List<ReceiptModal> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<ReceiptModal> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }
}