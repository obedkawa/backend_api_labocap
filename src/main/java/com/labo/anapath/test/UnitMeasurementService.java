package com.labo.anapath.test;

import com.labo.anapath.common.dto.PageResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface de service métier pour la gestion des unités de mesure.
 *
 * <p>Définit les opérations CRUD sur les unités de mesure du catalogue.</p>
 */
public interface UnitMeasurementService {

    /**
     * Retourne la liste paginée des unités de mesure d'une succursale.
     *
     * @param page     numéro de page (0-indexé)
     * @param size     taille de la page
     * @param branchId identifiant de la succursale
     * @return page de DTOs d'unités de mesure
     */
    PageResponse<UnitMeasurementResponseDto> findAll(int page, int size, UUID branchId);

    List<UnitMeasurementResponseDto> findAll(UUID branchId);

    /**
     * Recherche une unité de mesure par son identifiant.
     *
     * @param id identifiant UUID de l'unité
     * @return le DTO de l'unité trouvée
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si l'unité n'existe pas
     */
    UnitMeasurementResponseDto findById(UUID id);

    /**
     * Crée une nouvelle unité de mesure dans la succursale spécifiée.
     *
     * @param dto      données de création
     * @param branchId identifiant de la succursale
     * @return le DTO de l'unité créée
     */
    UnitMeasurementResponseDto create(UnitMeasurementRequestDto dto, UUID branchId);

    /**
     * Met à jour une unité de mesure existante.
     *
     * @param id  identifiant UUID de l'unité
     * @param dto nouvelles données
     * @return le DTO mis à jour
     */
    UnitMeasurementResponseDto update(UUID id, UnitMeasurementRequestDto dto);

    /**
     * Supprime logiquement une unité de mesure (soft-delete).
     * La suppression est refusée si des analyses ({@link LabTest}) référencent cette unité.
     *
     * @param id identifiant UUID de l'unité à supprimer
     * @throws com.labo.anapath.common.exception.BusinessException si l'unité est référencée
     */
    void delete(UUID id);
}
