package com.labo.anapath.testorder;

/**
 * DTO de statistiques pour l'espace personnel d'un technicien/pathologiste.
 *
 * <p>Agrège les compteurs des bons d'examen assignés à l'utilisateur connecté
 * pour la branche courante.
 *
 * @param totalAssigned  nombre total de bons assignés à l'utilisateur
 * @param totalPending   nombre de bons assignés au statut PENDING
 * @param totalValidated nombre de bons assignés au statut VALIDATED
 * @param totalUrgent    nombre de bons assignés marqués comme urgents
 * @param totalLate      nombre de bons assignés en retard (PENDING depuis plus de 48 h)
 */
public record MyspaceStatsDto(
        long totalAssigned,
        long totalPending,
        long totalValidated,
        long totalUrgent,
        long totalLate
) {}
