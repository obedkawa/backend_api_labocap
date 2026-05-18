package com.labo.anapath.doctor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse représentant un médecin prescripteur renvoyé au client HTTP.
 *
 * @param id         identifiant unique du médecin
 * @param name       nom complet du médecin
 * @param telephone  numéro de téléphone (peut être {@code null})
 * @param email      adresse e-mail (peut être {@code null})
 * @param role       spécialité ou fonction (peut être {@code null})
 * @param commission taux de commission (peut être {@code null})
 * @param branchId   identifiant de l'agence à laquelle le médecin est rattaché
 * @param createdAt  date et heure de création
 */
public record DoctorResponseDto(
        UUID id,
        String name,
        String telephone,
        String email,
        String role,
        Double commission,
        UUID branchId,
        LocalDateTime createdAt
) {}
