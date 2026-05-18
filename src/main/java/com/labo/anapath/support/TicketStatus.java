package com.labo.anapath.support;

/**
 * Enumération représentant le statut d'un ticket de support interne.
 * <ul>
 *   <li>{@link #OPEN} – ticket ouvert, en attente de traitement (statut initial)</li>
 *   <li>{@link #IN_PROGRESS} – ticket en cours de traitement</li>
 *   <li>{@link #RESOLVED} – ticket résolu, en attente de confirmation</li>
 *   <li>{@link #CLOSED} – ticket clôturé définitivement</li>
 * </ul>
 */
public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}
