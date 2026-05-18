package com.labo.anapath.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefundReasonRepository extends JpaRepository<RefundReason, UUID> {
}
