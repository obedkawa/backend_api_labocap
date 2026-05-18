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
public interface BankDepositRepository extends JpaRepository<BankDeposit, UUID> {

    Page<BankDeposit> findByBranchId(UUID branchId, Pageable pageable);

    @Query("SELECT d FROM BankDeposit d WHERE d.branchId = :branchId " +
           "AND (:bankId IS NULL OR d.bank.id = :bankId) " +
           "AND (:date IS NULL OR d.date = :date) " +
           "ORDER BY d.date DESC, d.createdAt DESC")
    Page<BankDeposit> findWithFilters(
            @Param("branchId") UUID branchId,
            @Param("bankId") UUID bankId,
            @Param("date") LocalDate date,
            Pageable pageable);
}
