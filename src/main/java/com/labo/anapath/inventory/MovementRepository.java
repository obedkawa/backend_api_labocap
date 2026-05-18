package com.labo.anapath.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'accès aux mouvements de stock.
 */
@Repository
public interface MovementRepository extends JpaRepository<Movement, UUID> {

    /**
     * Retourne une page de mouvements de stock filtrés par filiale.
     *
     * @param branchId identifiant de la filiale
     * @param pageable paramètres de pagination
     * @return page de mouvements
     */
    Page<Movement> findByBranchId(UUID branchId, Pageable pageable);
}
