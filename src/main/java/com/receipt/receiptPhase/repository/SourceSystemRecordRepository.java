package com.receipt.receiptPhase.repository;

import com.receipt.receiptPhase.model.SourceSystemRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceSystemRecordRepository extends JpaRepository<SourceSystemRecord, String> {


    @Query(value = "SELECT MIN(bl_no) as id, vessel_code as vesselCode, vessel_name as vesselName " +
            "FROM source_system_records " +
            "WHERE (:customerName IS NULL OR :customerName = '' OR customer_name = :customerName) " +
            "AND (:search IS NULL OR :search = '' OR vessel_name LIKE %:search% OR vessel_code LIKE %:search%) " +
            "GROUP BY vessel_code, vessel_name",
            nativeQuery = true)
    List<VesselProjection> findVessels(@Param("customerName") String customerName,
                                       @Param("search") String search);


    @Query(value = "SELECT MIN(bl_no) as id, voyage_no as voyageNo " +
            "FROM source_system_records " +
            "WHERE (:customerName IS NULL OR :customerName = '' OR customer_name = :customerName) " +
            "AND (:vessel IS NULL OR :vessel = '' OR vessel_name = :vessel OR vessel_code = :vessel) " +
            "AND (:search IS NULL OR :search = '' OR voyage_no LIKE %:search%) " +
            "GROUP BY voyage_no",
            nativeQuery = true)
    List<VoyageProjection> findVoyages(@Param("customerName") String customerName,
                                       @Param("vessel") String vessel,
                                       @Param("search") String search);
}