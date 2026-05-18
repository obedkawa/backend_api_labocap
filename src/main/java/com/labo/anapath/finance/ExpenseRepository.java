package com.labo.anapath.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    Page<Expense> findByBranchId(UUID branchId, Pageable pageable);

    Page<Expense> findByBranchIdAndPaid(UUID branchId, Integer paid, Pageable pageable);
}
