package com.labo.anapath.finance;

/**
 * Enumération des modes de paiement acceptés par le laboratoire.
 *
 * <p>Le mode {@link #MOBILE_MONEY} couvre les opérateurs MTN et MOOV
 * via l'API Sckaler (préfixe Bénin «&nbsp;229&nbsp;»).</p>
 */
public enum PaymentMethod {
    /** Paiement en espèces à la caisse. */
    CASH,
    /** Paiement par carte bancaire. */
    CARD,
    /** Virement bancaire. */
    TRANSFER,
    /** Paiement par chèque. */
    CHECK,
    /** Paiement Mobile Money (MTN / MOOV via Sckaler API). */
    MOBILE_MONEY
}
