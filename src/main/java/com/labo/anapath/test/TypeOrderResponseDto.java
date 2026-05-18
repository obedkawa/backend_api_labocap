package com.labo.anapath.test;

import java.util.UUID;

/**
 * DTO de réponse représentant un type de bon de demande exposé par l'API.
 *
 * @param id       identifiant unique du type de bon
 * @param title    titre lisible (ex. : "Biopsie")
 * @param slug     slug technique unique (ex. : "biopsie")
 * @param branchId identifiant de la succursale propriétaire
 */
public record TypeOrderResponseDto(UUID id, String title, String slug, UUID branchId) {}
