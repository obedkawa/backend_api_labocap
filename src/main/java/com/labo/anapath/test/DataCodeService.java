package com.labo.anapath.test;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

/**
 * Interface de service métier pour la gestion des codes de référence (DataCode).
 *
 * <p>Définit les opérations CRUD sur les codes de référence du catalogue.</p>
 */
public interface DataCodeService {

    /**
     * Retourne la liste paginée des codes de référence d'une succursale.
     *
     * @param page     numéro de page (0-indexé)
     * @param size     taille de la page
     * @param branchId identifiant de la succursale
     * @return page de DTOs de codes de référence
     */
    PageResponse<DataCodeResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Recherche un code de référence par son identifiant.
     *
     * @param id identifiant UUID du code
     * @return le DTO du code trouvé
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si le code n'existe pas
     */
    DataCodeResponseDto findById(UUID id);

    /**
     * Crée un nouveau code de référence dans la succursale spécifiée.
     *
     * @param dto      données de création
     * @param branchId identifiant de la succursale
     * @return le DTO du code créé
     */
    DataCodeResponseDto create(DataCodeRequestDto dto, UUID branchId);

    /**
     * Met à jour un code de référence existant.
     *
     * @param id  identifiant UUID du code
     * @param dto nouvelles données
     * @return le DTO mis à jour
     */
    DataCodeResponseDto update(UUID id, DataCodeRequestDto dto);

    /**
     * Supprime logiquement un code de référence (soft-delete).
     *
     * @param id identifiant UUID du code à supprimer
     */
    void delete(UUID id);
}
