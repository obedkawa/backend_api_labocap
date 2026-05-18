package com.labo.anapath.report;

/**
 * Statuts possibles d'un compte-rendu anatomopathologique ({@link Report}),
 * représentant son cycle de vie de la rédaction à la remise au patient.
 */
public enum ReportStatus {
    /** Compte-rendu en cours de rédaction, modifiable par le pathologiste. */
    DRAFT,
    /** Compte-rendu soumis pour relecture avant validation officielle. */
    PENDING_REVIEW,
    /** Compte-rendu validé et signé par le pathologiste responsable. */
    VALIDATED,
    /** Compte-rendu remis physiquement au patient ou à son représentant. */
    DELIVERED
}
