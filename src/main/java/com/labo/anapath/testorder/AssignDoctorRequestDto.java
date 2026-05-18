package com.labo.anapath.testorder;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO de requête pour l'assignation d'un médecin pathologiste à un bon d'examen.
 *
 * @param doctorId identifiant UUID du médecin (utilisateur) à assigner — obligatoire
 */
public record AssignDoctorRequestDto(
        @NotNull UUID doctorId
) {}
