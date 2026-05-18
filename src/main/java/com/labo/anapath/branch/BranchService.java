package com.labo.anapath.branch;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

/**
 * Contrat de service pour la gestion des agences du laboratoire.
 * <p>
 * Définit les opérations CRUD disponibles. L'implémentation
 * ({@link BranchServiceImpl}) porte les règles métier (unicité du nom,
 * vérification des dépendances avant suppression, etc.).
 * </p>
 */
public interface BranchService {

    /**
     * Retourne la liste paginée des agences, triée par date de création décroissante.
     *
     * @param page numéro de page (commence à 0)
     * @param size nombre d'éléments par page
     * @return page de {@link BranchResponseDto}
     */
    PageResponse<BranchResponseDto> findAll(int page, int size);

    /**
     * Recherche une agence par son identifiant unique.
     *
     * @param id identifiant UUID de l'agence
     * @return le DTO de l'agence
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si l'agence n'existe pas
     */
    BranchResponseDto findById(UUID id);

    /**
     * Crée une nouvelle agence à partir des données fournies.
     *
     * @param dto données de création (nom obligatoire)
     * @return le DTO de l'agence créée
     * @throws com.labo.anapath.common.exception.DuplicateResourceException si le nom est déjà utilisé
     */
    BranchResponseDto create(BranchRequestDto dto);

    /**
     * Met à jour une agence existante.
     *
     * @param id  identifiant de l'agence à modifier
     * @param dto nouvelles données
     * @return le DTO de l'agence mise à jour
     */
    BranchResponseDto update(UUID id, BranchRequestDto dto);

    /**
     * Supprime logiquement une agence.
     * La suppression est refusée si des utilisateurs lui sont rattachés.
     *
     * @param id identifiant de l'agence à supprimer
     * @throws com.labo.anapath.common.exception.BusinessException si l'agence a des dépendances
     */
    void delete(UUID id);
}
