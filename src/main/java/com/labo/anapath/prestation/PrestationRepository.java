package com.labo.anapath.prestation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PrestationRepository extends JpaRepository<Prestation, UUID> {

    Page<Prestation> findByBranchId(UUID branchId, Pageable pageable);

    Page<Prestation> findByCategoryPrestationIdAndBranchId(UUID categoryId, UUID branchId, Pageable pageable);
}
