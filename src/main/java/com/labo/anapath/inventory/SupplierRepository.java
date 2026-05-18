package com.labo.anapath.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    Page<Supplier> findByBranchId(UUID branchId, Pageable pageable);
    List<Supplier> findByBranchIdAndNameContainingIgnoreCase(UUID branchId, String name);
    boolean existsBySupplierCategory(SupplierCategory supplierCategory);
}
