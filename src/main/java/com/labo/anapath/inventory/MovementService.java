package com.labo.anapath.inventory;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

/**
 * Interface de service définissant les opérations métier sur les mouvements de stock.
 */
public interface MovementService {

    /**
     * Retourne la liste paginée des mouvements de stock d'une filiale.
     *
     * @param page     numéro de page
     * @param size     taille de la page
     * @param branchId identifiant de la filiale
     * @return page de mouvements
     */
    PageResponse<MovementResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Enregistre un nouveau mouvement de stock et met à jour la quantité de l'article.
     *
     * @param dto      données du mouvement (type, article, quantité, notes)
     * @param branchId identifiant de la filiale
     * @return le mouvement créé
     * @throws com.labo.anapath.common.exception.BusinessException si le stock est insuffisant pour une sortie
     */
    MovementResponseDto create(MovementRequestDto dto, UUID branchId);
}
