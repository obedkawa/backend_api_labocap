package com.labo.anapath.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// Note: "ExpenceDetail" (avec 'c') — typo métier issu de Laravel, à préserver
@Repository
public interface ExpenceDetailRepository extends JpaRepository<ExpenceDetail, UUID> {

    @org.springframework.data.jpa.repository.Query("SELECT d FROM ExpenceDetail d WHERE d.expense.id = :expenseId")
    List<ExpenceDetail> findByExpenseId(@org.springframework.data.repository.query.Param("expenseId") UUID expenseId);
}
