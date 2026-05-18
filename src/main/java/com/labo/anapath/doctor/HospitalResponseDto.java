package com.labo.anapath.doctor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse représentant un hôpital renvoyé au client HTTP.
 *
 * @param id         identifiant unique de l'hôpital
 * @param name       nom officiel de l'établissement
 * @param telephone  numéro de téléphone principal
 * @param adresse    adresse physique (peut être {@code null})
 * @param email      adresse e-mail de contact (peut être {@code null})
 * @param commission taux de commission (peut être {@code null})
 * @param branchId   identifiant de l'agence à laquelle l'hôpital est rattaché
 * @param createdAt  date et heure de création
 */
public record HospitalResponseDto(
        UUID id,
        String name,
        String telephone,
        String adresse,
        String email,
        Double commission,
        UUID branchId,
        LocalDateTime createdAt
) {}
