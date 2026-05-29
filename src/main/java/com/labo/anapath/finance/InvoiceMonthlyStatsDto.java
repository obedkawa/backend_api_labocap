package com.labo.anapath.finance;

import java.math.BigDecimal;

/**
 * Statistiques d'un mois pour le tableau Rapports.
 * Réplique Laravel : Mois | Facturés | Avoirs | Chiffre d'affaires | Encaissements
 */
public record InvoiceMonthlyStatsDto(
        int month,              // numéro 1-12
        int year,
        String monthName,       // "Janvier", "Février"...
        BigDecimal facturated,  // total facturé (status_invoice=0)
        BigDecimal credits,     // total avoirs payés (status_invoice=1, paid=true)
        BigDecimal turnover,    // CA = ventes payées (status_invoice=0, paid=true)
        BigDecimal collections  // turnover - credits
) {}
