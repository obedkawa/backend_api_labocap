package com.labo.anapath.branch;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse renvoyé au client pour représenter une agence.
 * <p>
 * Contient uniquement les informations publiques de l'agence ; les champs
 * techniques ({@code updatedAt}, {@code deletedAt}) ne sont pas exposés.
 * </p>
 *
 * @param id        identifiant unique de l'agence
 * @param name      nom de l'agence
 * @param code      code interne de l'agence
 * @param location  localisation géographique
 * @param createdAt date et heure de création
 */
public record BranchResponseDto(
        UUID id,
        String name,
        String code,
        String location,
        LocalDateTime createdAt
) {}
