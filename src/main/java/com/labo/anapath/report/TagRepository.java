package com.labo.anapath.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'entité {@link Tag}.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    /**
     * Retourne la liste paginée des tags d'une branche.
     * Assure l'isolation multi-tenant : un tag n'est visible que par sa branche propriétaire.
     *
     * @param branchId identifiant de la branche
     * @param pageable paramètres de pagination
     * @return page de tags
     */
    Page<Tag> findByBranchId(UUID branchId, Pageable pageable);
}
