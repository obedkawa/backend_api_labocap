package com.labo.anapath.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'entité {@link TestPathologyMacro}.
 */
@Repository
public interface TestPathologyMacroRepository extends JpaRepository<TestPathologyMacro, UUID> {

    /**
     * Retourne la liste paginée des macros d'une branche.
     * Assure l'isolation multi-tenant : les macros ne sont visibles que par leur branche propriétaire.
     *
     * @param branchId identifiant de la branche
     * @param pageable paramètres de pagination
     * @return page de macros anatomopathologiques
     */
    Page<TestPathologyMacro> findByBranchId(UUID branchId, Pageable pageable);

    @Query("SELECT m FROM TestPathologyMacro m WHERE m.branchId = :branchId " +
           "AND (LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<TestPathologyMacro> search(@Param("branchId") UUID branchId, @Param("q") String q, Pageable pageable);

    Optional<TestPathologyMacro> findByTestOrderId(UUID testOrderId);
}
