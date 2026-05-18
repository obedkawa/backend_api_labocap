package com.labo.anapath.test;

import java.util.UUID;

/**
 * DTO de réponse représentant une unité de mesure exposée par l'API.
 *
 * @param id           identifiant unique de l'unité de mesure
 * @param name         nom complet (ex. : "milligramme par litre")
 * @param abbreviation abréviation standard (ex. : "mg/L")
 * @param branchId     identifiant de la succursale propriétaire
 */
public record UnitMeasurementResponseDto(UUID id, String name, String abbreviation, UUID branchId) {}
