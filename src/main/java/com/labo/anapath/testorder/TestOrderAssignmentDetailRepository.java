package com.labo.anapath.testorder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestOrderAssignmentDetailRepository extends JpaRepository<TestOrderAssignmentDetail, UUID> {

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END FROM TestOrderAssignmentDetail d WHERE d.testOrder.id = :testOrderId")
    boolean existsByTestOrderId(@Param("testOrderId") UUID testOrderId);

    @Query("SELECT d FROM TestOrderAssignmentDetail d WHERE d.testOrder.id = :testOrderId")
    Optional<TestOrderAssignmentDetail> findByTestOrderId(@Param("testOrderId") UUID testOrderId);
}
