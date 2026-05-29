package com.labo.anapath.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    Page<Expense> findByBranchId(UUID branchId, Pageable pageable);

    Page<Expense> findByBranchIdAndPaid(UUID branchId, Integer paid, Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE e.branchId = :branchId " +
           "AND (:paid IS NULL OR e.paid = :paid) " +
           "AND (:expenseCategorieId IS NULL OR e.expenseCategorieId = :expenseCategorieId)")
    Page<Expense> findWithFilters(@Param("branchId") UUID branchId,
                                  @Param("paid") Integer paid,
                                  @Param("expenseCategorieId") UUID expenseCategorieId,
                                  Pageable pageable);
}
