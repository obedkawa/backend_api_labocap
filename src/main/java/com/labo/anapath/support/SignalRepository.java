package com.labo.anapath.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SignalRepository extends JpaRepository<Signal, UUID> {
    Page<Signal> findByBranchId(UUID branchId, Pageable pageable);
}
