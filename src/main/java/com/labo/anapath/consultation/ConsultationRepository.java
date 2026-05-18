package com.labo.anapath.consultation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, UUID> {

    Page<Consultation> findByBranchId(UUID branchId, Pageable pageable);

    long countByBranchId(UUID branchId);

    boolean existsByPrestationId(UUID prestationId);

    @Query("SELECT c FROM Consultation c WHERE c.branchId = :branchId " +
           "AND (:patientId IS NULL OR c.patient.id = :patientId) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:doctorId IS NULL OR c.attribuateDoctor.id = :doctorId)")
    Page<Consultation> findWithFilters(
            @Param("branchId") UUID branchId,
            @Param("patientId") UUID patientId,
            @Param("status") String status,
            @Param("doctorId") UUID doctorId,
            Pageable pageable);
}
