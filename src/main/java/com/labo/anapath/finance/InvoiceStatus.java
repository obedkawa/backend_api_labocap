package com.labo.anapath.finance;

/**
 * Enumération des statuts possibles d'une facture.
 *
 * <ul>
 *   <li>{@link #PENDING} : facture émise, en attente de règlement</li>
 *   <li>{@link #PAID} : facture intégralement réglée</li>
 *   <li>{@link #PARTIALLY_PAID} : acompte reçu, solde restant dû</li>
 *   <li>{@link #CANCELLED} : facture annulée (aucun encaissement attendu)</li>
 * </ul>
 */
public enum InvoiceStatus {
    /** Facture émise, en attente de paiement. */
    PENDING,
    /** Facture entièrement payée. */
    PAID,
    /** Paiement partiel reçu ; un solde reste à régler. */
    PARTIALLY_PAID,
    /** Facture annulée. */
    CANCELLED
}
