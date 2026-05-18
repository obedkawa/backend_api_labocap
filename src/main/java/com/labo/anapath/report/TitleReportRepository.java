package com.labo.anapath.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'entité {@link TitleReport}.
 */
@Repository
public interface TitleReportRepository extends JpaRepository<TitleReport, UUID> {

    /**
     * Retourne la liste paginée des titres de rapport d'une branche.
     * Assure l'isolation multi-tenant.
     *
     * @param branchId identifiant de la branche
     * @param pageable paramètres de pagination
     * @return page de titres de rapport
     */
    Page<TitleReport> findByBranchId(UUID branchId, Pageable pageable);
}
