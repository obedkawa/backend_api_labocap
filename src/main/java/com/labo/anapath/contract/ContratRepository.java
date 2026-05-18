package com.labo.anapath.contract;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Contrat}.
 */
@Repository
public interface ContratRepository extends JpaRepository<Contrat, UUID> {

    /**
     * Retourne tous les contrats d'une agence avec pagination.
     *
     * @param branchId identifiant de l'agence
     * @param pageable paramètres de pagination et de tri
     * @return page de contrats
     */
    Page<Contrat> findByBranchId(UUID branchId, Pageable pageable);

    /**
     * Vérifie si un contrat existe déjà pour un client donné.
     * Utile pour éviter les doublons lors de la création d'un nouveau contrat.
     *
     * @param clientId identifiant du client institutionnel
     * @return {@code true} si un contrat actif existe pour ce client
     */
    boolean existsByClientId(UUID clientId);
}
