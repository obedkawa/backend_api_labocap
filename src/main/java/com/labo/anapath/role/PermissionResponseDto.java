package com.labo.anapath.role;

import java.util.UUID;

/**
 * DTO de réponse représentant une permission exposée par l'API.
 *
 * @param id   identifiant unique de la permission
 * @param name libellé lisible (ex. : "Gérer les utilisateurs")
 * @param slug slug technique utilisé dans les vérifications Spring Security
 *             (ex. : {@code manage-users})
 */
public record PermissionResponseDto(
        UUID id,
        String name,
        String slug
) {}
