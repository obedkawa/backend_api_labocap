package com.labo.anapath.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemCategoryRepository extends JpaRepository<ProblemCategory, UUID> {
    List<ProblemCategory> findByBranchId(UUID branchId);
    Optional<ProblemCategory> findByNameAndBranchId(String name, UUID branchId);
}
