package com.labo.anapath.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProblemReportRepository extends JpaRepository<ProblemReport, UUID> {
    Page<ProblemReport> findByBranchId(UUID branchId, Pageable pageable);
}
