package com.labo.anapath.support;

/**
 * Enumération représentant le niveau de priorité d'un ticket de support.
 * <ul>
 *   <li>{@link #LOW} – faible priorité, impact mineur</li>
 *   <li>{@link #MEDIUM} – priorité normale (valeur par défaut à la création)</li>
 *   <li>{@link #HIGH} – haute priorité, traitement urgent requis</li>
 *   <li>{@link #CRITICAL} – bloquant, impact majeur sur le fonctionnement du labo</li>
 * </ul>
 */
public enum TicketPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
