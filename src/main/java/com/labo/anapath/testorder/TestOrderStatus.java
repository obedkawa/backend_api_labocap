package com.labo.anapath.testorder;

/**
 * Statuts possibles d'un bon d'examen anatomopathologique, représentant sa machine d'état.
 *
 * <p>Correspondance avec le système Laravel source :
 * <ul>
 *   <li>{@link #PENDING} correspond à {@code status=false} dans Laravel</li>
 *   <li>{@link #VALIDATED} correspond à {@code status=true} dans Laravel</li>
 *   <li>{@link #DELIVERED} correspond à {@code Report.is_delivered=true} dans Laravel</li>
 * </ul>
 */
public enum TestOrderStatus {
    /** Bon créé, en attente de validation par un technicien. Aucun code généré à ce stade. */
    PENDING,    // status=false dans Laravel (NOUVEAU — bon créé, non validé)
    /** Bon validé : code unique généré, compte-rendu DRAFT créé, facture émise. */
    VALIDATED,  // status=true dans Laravel (VALIDÉ — code généré, Report créé)
    /** Résultats remis au patient ou à son représentant. */
    DELIVERED,  // Report.is_delivered=true (livré au patient)
    /** Bon annulé. Aucune modification ultérieure n'est possible. */
    CANCELLED
}
