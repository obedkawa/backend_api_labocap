package com.labo.anapath.setting;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

/**
 * Interface de service définissant les opérations métier sur la configuration du laboratoire.
 */
public interface SettingService {

    /**
     * Retourne la liste paginée des paramètres d'une filiale.
     *
     * @param page     numéro de page
     * @param size     taille de la page
     * @param branchId identifiant de la filiale
     * @return page de paramètres
     */
    PageResponse<SettingResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Retourne un paramètre par son identifiant.
     *
     * @param id identifiant UUID du paramètre
     * @return le paramètre correspondant
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si introuvable
     */
    SettingResponseDto findById(UUID id);

    /**
     * Crée ou met à jour un paramètre pour une filiale (upsert).
     * Si un paramètre avec la même clé existe déjà pour la filiale,
     * sa valeur est mise à jour. Sinon, un nouveau paramètre est créé.
     *
     * @param dto      données du paramètre
     * @param branchId identifiant de la filiale
     * @return le paramètre sauvegardé
     */
    SettingResponseDto upsert(SettingRequestDto dto, UUID branchId);

    /**
     * Supprime (logiquement) un paramètre.
     *
     * @param id identifiant UUID du paramètre à supprimer
     */
    void delete(UUID id);
}
