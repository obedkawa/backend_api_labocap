package com.labo.anapath.prestationorder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PrestationOrderRepository extends JpaRepository<PrestationOrder, UUID> {

    Page<PrestationOrder> findByBranchId(UUID branchId, Pageable pageable);

    boolean existsByPrestationId(UUID prestationId);
}
