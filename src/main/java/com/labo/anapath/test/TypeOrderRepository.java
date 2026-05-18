package com.labo.anapath.test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link TypeOrder}.
 *
 * <p>Les requêtes sont automatiquement filtrées par {@code deleted_at IS NULL}
 * grâce à la restriction Hibernate définie sur l'entité.</p>
 */
@Repository
public interface TypeOrderRepository extends JpaRepository<TypeOrder, UUID> {

    /**
     * Retourne la liste paginée des types de bons d'une succursale donnée.
     *
     * @param branchId identifiant de la succursale
     * @param pageable paramètres de pagination et de tri
     * @return page de types de bons
     */
    Page<TypeOrder> findByBranchId(UUID branchId, Pageable pageable);

    /**
     * Recherche un type de bon par son slug technique.
     * Utilisé lors de la création de bons de demande pour identifier le type.
     *
     * @param slug slug du type de bon
     * @return un {@link Optional} contenant le type trouvé, ou vide
     */
    Optional<TypeOrder> findBySlug(String slug);

    /**
     * Vérifie si un type de bon avec ce slug existe dans la succursale (insensible à la casse).
     * Utilisé pour détecter les doublons lors de la création.
     *
     * @param slug     slug à vérifier
     * @param branchId identifiant de la succursale
     * @return {@code true} si le slug est déjà utilisé dans la succursale
     */
    boolean existsBySlugIgnoreCaseAndBranchId(String slug, UUID branchId);

    /**
     * Vérifie si un type de bon avec ce slug existe dans la succursale, en excluant
     * celui identifié par {@code id}. Utilisé pour détecter les doublons lors d'une mise à jour.
     *
     * @param slug     slug à vérifier
     * @param branchId identifiant de la succursale
     * @param id       identifiant du type à exclure
     * @return {@code true} si le slug est déjà utilisé par un autre type
     */
    boolean existsBySlugIgnoreCaseAndBranchIdAndIdNot(String slug, UUID branchId, UUID id);
}
