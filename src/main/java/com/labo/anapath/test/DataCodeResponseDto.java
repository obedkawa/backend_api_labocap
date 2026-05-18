package com.labo.anapath.test;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse représentant un code de référence exposé par l'API.
 *
 * @param id        identifiant unique du code
 * @param code      code technique optionnel (ex. : code SNOMED)
 * @param label     libellé lisible du code
 * @param type      type de code (ex. : "SNOMED", "valeur_normale")
 * @param branchId  identifiant de la succursale propriétaire
 * @param createdAt date et heure de création
 */
public record DataCodeResponseDto(UUID id, String code, String label, String type, UUID branchId, LocalDateTime createdAt) {}
