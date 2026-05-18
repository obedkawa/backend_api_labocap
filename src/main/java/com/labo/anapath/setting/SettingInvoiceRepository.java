package com.labo.anapath.setting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettingInvoiceRepository extends JpaRepository<SettingInvoice, UUID> {

    Page<SettingInvoice> findByBranchId(UUID branchId, Pageable pageable);

    Optional<SettingInvoice> findFirstByBranchId(UUID branchId);
}
