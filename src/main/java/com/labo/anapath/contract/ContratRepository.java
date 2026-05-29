package com.labo.anapath.contract;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, UUID> {

    Page<Contrat> findByBranchId(UUID branchId, Pageable pageable);

    java.util.Optional<Contrat> findByIdAndBranchId(UUID id, UUID branchId);

    boolean existsByClientId(UUID clientId);

    // Dashboard KPIs
    long countByBranchId(UUID branchId);

    @Query("SELECT c FROM Contrat c WHERE c.branchId = :branchId " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:dateFrom IS NULL OR c.startDate >= :dateFrom) " +
           "AND (:dateTo IS NULL OR c.startDate <= :dateTo)")
    Page<Contrat> findWithFilters(@Param("branchId") UUID branchId,
                                  @Param("status") String status,
                                  @Param("search") String search,
                                  @Param("dateFrom") LocalDate dateFrom,
                                  @Param("dateTo") LocalDate dateTo,
                                  Pageable pageable);
}
