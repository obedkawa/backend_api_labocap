package com.labo.anapath.client;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse représentant un client institutionnel renvoyé au client HTTP.
 * <p>
 * Le {@code branchId} est inclus pour permettre au front-end de savoir à quelle
 * agence le client est rattaché.
 * </p>
 *
 * @param id        identifiant unique du client
 * @param ifu       numéro IFU (peut être {@code null})
 * @param name      raison sociale ou nom du client
 * @param adress    adresse physique (peut être {@code null})
 * @param contact   coordonnées de contact (peut être {@code null})
 * @param branchId  identifiant de l'agence à laquelle le client appartient
 * @param createdAt date et heure de création
 */
public record ClientResponseDto(
        UUID id,
        String ifu,
        String name,
        String adress,
        String contact,
        UUID branchId,
        LocalDateTime createdAt
) {}
