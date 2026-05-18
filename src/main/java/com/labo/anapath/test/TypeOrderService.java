package com.labo.anapath.test;

import com.labo.anapath.common.dto.PageResponse;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface de service métier pour la gestion des types de bons de demande (TypeOrder).
 *
 * <p>Définit les opérations CRUD et la recherche par slug sur les types de bons.</p>
 */
public interface TypeOrderService {

    /**
     * Retourne la liste paginée des types de bons d'une succursale.
     *
     * @param page     numéro de page (0-indexé)
     * @param size     taille de la page
     * @param branchId identifiant de la succursale
     * @return page de DTOs de types de bons
     */
    PageResponse<TypeOrderResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Recherche un type de bon par son identifiant.
     *
     * @param id identifiant UUID du type de bon
     * @return le DTO du type trouvé
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si le type n'existe pas
     */
    TypeOrderResponseDto findById(UUID id);

    /**
     * Recherche un type de bon par son slug technique.
     * Utilisé lors de la création de bons de demande pour résoudre le type.
     *
     * @param slug slug du type de bon
     * @return un {@link Optional} contenant le DTO du type trouvé, ou vide
     */
    Optional<TypeOrderResponseDto> findBySlug(String slug);

    /**
     * Crée un nouveau type de bon dans la succursale spécifiée.
     *
     * @param dto      données de création (titre et slug obligatoires)
     * @param branchId identifiant de la succursale
     * @return le DTO du type créé
     * @throws com.labo.anapath.common.exception.DuplicateResourceException si le slug existe déjà
     */
    TypeOrderResponseDto create(TypeOrderRequestDto dto, UUID branchId);

    /**
     * Met à jour un type de bon existant.
     *
     * @param id  identifiant UUID du type de bon
     * @param dto nouvelles données
     * @return le DTO mis à jour
     */
    TypeOrderResponseDto update(UUID id, TypeOrderRequestDto dto);

    /**
     * Supprime logiquement un type de bon (soft-delete).
     *
     * @param id identifiant UUID du type à supprimer
     */
    void delete(UUID id);
}
