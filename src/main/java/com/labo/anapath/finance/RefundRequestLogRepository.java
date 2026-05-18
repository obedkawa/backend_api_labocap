package com.labo.anapath.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefundRequestLogRepository extends JpaRepository<RefundRequestLog, UUID> {

    List<RefundRequestLog> findByRefundRequestId(UUID refundRequestId);
}
