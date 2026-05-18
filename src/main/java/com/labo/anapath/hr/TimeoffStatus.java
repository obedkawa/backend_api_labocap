package com.labo.anapath.hr;

/**
 * Enumération représentant le statut d'une demande de congé.
 * <ul>
 *   <li>{@link #PENDING} – demande en attente de traitement</li>
 *   <li>{@link #APPROVED} – demande approuvée par la direction</li>
 *   <li>{@link #REJECTED} – demande refusée par la direction</li>
 * </ul>
 */
public enum TimeoffStatus {
    PENDING,
    APPROVED,
    REJECTED
}
