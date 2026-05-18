package com.labo.anapath.finance;

import com.labo.anapath.patient.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Page<Invoice> findByBranchId(UUID branchId, Pageable pageable);

    List<Invoice> findByPatientOrderByCreatedAtDesc(Patient patient);

    Optional<Invoice> findByTestOrderId(UUID testOrderId);

    Optional<Invoice> findFirstByContratIdOrderByCreatedAtDesc(UUID contratId);

    @Query("SELECT i FROM Invoice i WHERE i.branchId = :branchId AND i.code IS NOT NULL " +
           "AND i.code <> 'REGULARISATION' AND YEAR(i.createdAt) = :year ORDER BY i.code DESC")
    List<Invoice> findByBranchIdAndCodeNotNullAndYear(
            @Param("branchId") UUID branchId,
            @Param("year") int year,
            Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.branchId = :branchId AND i.statusInvoice = :statusInvoice " +
           "AND i.code IS NOT NULL AND YEAR(i.createdAt) = :year ORDER BY i.code DESC")
    List<Invoice> findByBranchIdAndStatusInvoiceAndCodeNotNullAndYear(
            @Param("branchId") UUID branchId,
            @Param("statusInvoice") int statusInvoice,
            @Param("year") int year,
            Pageable pageable);

    // Business dashboard — somme des factures payées pour un mois/année donnés
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM invoices " +
                   "WHERE branch_id = :branchId AND paid = true " +
                   "AND EXTRACT(MONTH FROM updated_at) = :month AND EXTRACT(YEAR FROM updated_at) = :year " +
                   "AND deleted_at IS NULL",
           nativeQuery = true)
    BigDecimal sumPaidByBranchIdAndMonth(
            @Param("branchId") UUID branchId,
            @Param("month") int month,
            @Param("year") int year);

    // Business dashboard — somme des factures payées pour une date donnée
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM invoices " +
                   "WHERE branch_id = :branchId AND paid = true " +
                   "AND DATE(updated_at) = :today AND deleted_at IS NULL",
           nativeQuery = true)
    BigDecimal sumPaidByBranchIdAndDate(
            @Param("branchId") UUID branchId,
            @Param("today") LocalDate today);

    // Recherche par période — somme ventes payées (statusInvoice=0)
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM invoices " +
                   "WHERE branch_id = :branchId AND paid = true AND status_invoice = 0 " +
                   "AND DATE(updated_at) >= :startDate AND DATE(updated_at) <= :endDate " +
                   "AND deleted_at IS NULL",
           nativeQuery = true)
    BigDecimal sumVenteByBranchIdAndDateRange(
            @Param("branchId") UUID branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Recherche par période — somme avoirs payés (statusInvoice=1)
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM invoices " +
                   "WHERE branch_id = :branchId AND paid = true AND status_invoice = 1 " +
                   "AND DATE(updated_at) >= :startDate AND DATE(updated_at) <= :endDate " +
                   "AND deleted_at IS NULL",
           nativeQuery = true)
    BigDecimal sumAvoirByBranchIdAndDateRange(
            @Param("branchId") UUID branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Optional<Invoice> findFirstByCodeMecefOrCodeNormalise(String codeMecef, String codeNormalise);

    // Recherche par période — total facturé (toutes factures, paid ou non)
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM invoices " +
                   "WHERE branch_id = :branchId " +
                   "AND DATE(updated_at) >= :startDate AND DATE(updated_at) <= :endDate " +
                   "AND deleted_at IS NULL",
           nativeQuery = true)
    BigDecimal sumTotalByBranchIdAndDateRange(
            @Param("branchId") UUID branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
