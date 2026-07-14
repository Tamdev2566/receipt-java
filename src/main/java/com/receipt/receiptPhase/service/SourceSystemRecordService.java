package com.receipt.receiptPhase.service;

import com.receipt.receiptPhase.dto.SearchRequest;
import com.receipt.receiptPhase.repository.SourceSystemRecordRepository;
import com.receipt.receiptPhase.repository.VesselProjection;
import com.receipt.receiptPhase.repository.VoyageProjection;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceSystemRecordService {

    private final SourceSystemRecordRepository repository;

    public SourceSystemRecordService(SourceSystemRecordRepository repository) {
        this.repository = repository;
    }

    public List<VesselProjection> getVessels(SearchRequest request) {
        return repository.findVessels(request.getCustomerName(), request.getSearch());
    }

    public List<VoyageProjection> getVoyages(SearchRequest request) {
        return repository.findVoyages(request.getCustomerName(), request.getVessel(), request.getSearch());
    }
}