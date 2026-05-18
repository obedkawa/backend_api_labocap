package com.labo.anapath.testorder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TestOrderAssignmentRepository extends JpaRepository<TestOrderAssignment, UUID> {

    long countByBranchId(UUID branchId);

    @Query("SELECT DISTINCT a FROM TestOrderAssignment a JOIN a.details d JOIN d.testOrder o JOIN o.typeOrder ty " +
           "WHERE a.branchId = :branchId AND ty.slug IN ('histologie','cytologie','biopsie','pièce-opératoire') " +
           "ORDER BY a.createdAt DESC")
    Page<TestOrderAssignment> findHistoCyto(@Param("branchId") UUID branchId, Pageable pageable);

    @Query("SELECT DISTINCT a FROM TestOrderAssignment a JOIN a.details d JOIN d.testOrder o JOIN o.typeOrder ty " +
           "WHERE a.branchId = :branchId AND ty.slug IN ('immuno-interne','immuno-exterme') " +
           "ORDER BY a.createdAt DESC")
    Page<TestOrderAssignment> findImmuno(@Param("branchId") UUID branchId, Pageable pageable);
}
