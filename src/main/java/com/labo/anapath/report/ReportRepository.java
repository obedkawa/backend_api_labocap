package com.labo.anapath.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    Page<Report> findByBranchId(UUID branchId, Pageable pageable);

    Optional<Report> findByTestOrderId(UUID testOrderId);

    @Query("SELECT r FROM Report r WHERE r.branchId = :branchId " +
           "AND (:month IS NULL OR FUNCTION('MONTH', r.signatureDate) = :month) " +
           "AND (:year IS NULL OR FUNCTION('YEAR', r.signatureDate) = :year) " +
           "AND (:doctorId IS NULL OR r.signatory1.id = :doctorId) " +
           "ORDER BY r.createdAt DESC")
    Page<Report> findFiltered(
            @Param("branchId") UUID branchId,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("doctorId") UUID doctorId,
            Pageable pageable);

    @Query(value = """
            SELECT
                SUM(CASE WHEN to2.title = 'Histologie'     THEN 1 ELSE 0 END) AS histologie,
                SUM(CASE WHEN to2.title = 'Immuno Externe' THEN 1 ELSE 0 END) AS immuno_externe,
                SUM(CASE WHEN to2.title = 'Immuno Interne' THEN 1 ELSE 0 END) AS immuno_interne,
                SUM(CASE WHEN to2.title = 'Cytologie'      THEN 1 ELSE 0 END) AS cytologie,
                COUNT(tor.id) AS total_general
            FROM test_orders tor
            JOIN type_orders to2 ON tor.type_order_id = to2.id
            WHERE tor.status = 'VALIDATED' AND tor.branch_id = :branchId
            AND (:month IS NULL OR EXTRACT(MONTH FROM tor.created_at) = :month)
            AND (:year  IS NULL OR EXTRACT(YEAR  FROM tor.created_at) = :year)
            """, nativeQuery = true)
    Object[] getExamenStats(
            @Param("branchId") UUID branchId,
            @Param("month") Integer month,
            @Param("year") Integer year);

    @Query(value = """
            SELECT
                SUM(CASE WHEN rep.status = 'DRAFT'     THEN 1 ELSE 0 END) AS attente,
                SUM(CASE WHEN rep.status = 'VALIDATED' THEN 1 ELSE 0 END) AS termine,
                SUM(CASE WHEN toad.test_order_id IS NOT NULL THEN 1 ELSE 0 END) AS affecte
            FROM reports rep
            JOIN test_orders tor ON tor.id = rep.test_order_id
            LEFT JOIN test_order_assignment_details toad ON toad.test_order_id = rep.test_order_id
            WHERE rep.branch_id = :branchId AND rep.deleted_at IS NULL
            """, nativeQuery = true)
    Object[] getRapportStats(@Param("branchId") UUID branchId);

    @Query(value = """
            SELECT
                SUM(CASE WHEN is_called    = TRUE  THEN 1 ELSE 0 END) AS called,
                SUM(CASE WHEN is_called    = FALSE THEN 1 ELSE 0 END) AS not_called,
                SUM(CASE WHEN is_delivered = TRUE  THEN 1 ELSE 0 END) AS deliver,
                SUM(CASE WHEN is_delivered = FALSE THEN 1 ELSE 0 END) AS not_deliver
            FROM reports
            WHERE branch_id = :branchId AND deleted_at IS NULL
            """, nativeQuery = true)
    Object[] getPatientCalledStats(@Param("branchId") UUID branchId);

    @Query(value = """
            SELECT DISTINCT EXTRACT(YEAR FROM created_at)::int AS y
            FROM test_orders
            WHERE branch_id = :branchId AND deleted_at IS NULL
            ORDER BY y DESC
            """, nativeQuery = true)
    List<Integer> findAvailableYears(@Param("branchId") UUID branchId);

    @Query(value = """
            SELECT COUNT(tpm.id)
            FROM test_pathology_macros tpm
            JOIN test_orders tor ON tpm.test_order_id = tor.id
            WHERE tor.branch_id = :branchId AND tpm.deleted_at IS NULL
            """, nativeQuery = true)
    Long countMacrosWithOrders(@Param("branchId") UUID branchId);
}
