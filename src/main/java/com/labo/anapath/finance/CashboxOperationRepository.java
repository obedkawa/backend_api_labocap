package com.labo.anapath.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface CashboxOperationRepository extends JpaRepository<CashboxOperation, UUID> {

    @Query("SELECT o FROM CashboxOperation o WHERE o.branchId = :branchId " +
           "AND (:cashboxId IS NULL OR o.cashbox.id = :cashboxId) " +
           "AND (:type IS NULL OR o.type = :type) " +
           "AND (:date IS NULL OR o.operationDate = :date) " +
           "ORDER BY o.operationDate DESC, o.createdAt DESC")
    Page<CashboxOperation> findWithFilters(
            @Param("branchId") UUID branchId,
            @Param("cashboxId") UUID cashboxId,
            @Param("type") String type,
            @Param("date") LocalDate date,
            Pageable pageable);
}
