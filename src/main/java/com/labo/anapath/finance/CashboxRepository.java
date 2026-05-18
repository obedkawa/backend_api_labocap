package com.labo.anapath.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Cashbox}.
 */
@Repository
public interface CashboxRepository extends JpaRepository<Cashbox, UUID> {

    Page<Cashbox> findByBranchId(UUID branchId, Pageable pageable);

    Optional<Cashbox> findFirstByBranchIdAndType(UUID branchId, String type);
}
