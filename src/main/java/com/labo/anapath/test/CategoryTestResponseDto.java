package com.labo.anapath.test;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse représentant une catégorie d'analyses exposée par l'API.
 *
 * @param id        identifiant unique de la catégorie
 * @param name      nom de la catégorie (ex. : "Hématologie")
 * @param code      code court optionnel (ex. : "HEM")
 * @param branchId  identifiant de la succursale propriétaire
 * @param createdAt date et heure de création
 */
public record CategoryTestResponseDto(UUID id, String name, String code, UUID branchId, LocalDateTime createdAt) {}
