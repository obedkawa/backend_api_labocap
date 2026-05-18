package com.labo.anapath.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, UUID> {

    Page<RefundRequest> findByBranchId(UUID branchId, Pageable pageable);

    boolean existsByInvoiceId(UUID invoiceId);

    Optional<RefundRequest> findByInvoiceId(UUID invoiceId);
}
