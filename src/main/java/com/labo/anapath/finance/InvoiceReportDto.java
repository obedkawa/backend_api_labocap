package com.labo.anapath.finance;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de réponse pour le rapport mensuel des factures.
 * <p>
 * Réplique la vue Laravel {@code invoice.reports} : pour une période (mois/année)
 * donnée, expose les totaux de ventes, avoirs, chiffre d'affaires (CA = ventes - avoirs)
 * et encaissements (factures de vente payées). Détail également des totaux par contrat.
 * </p>
 *
 * @param period       libellé de la période, ex. "Mai 2026"
 * @param totalSales   somme des factures de vente créées sur la période (status_invoice=0)
 * @param totalCredits somme des avoirs créés sur la période (status_invoice=1)
 * @param turnover     chiffre d'affaires (ventes - avoirs)
 * @param collections  encaissements réels (ventes payées sur la période)
 * @param byContracts  détail des ventes par contrat
 */
public record InvoiceReportDto(
        String period,
        BigDecimal totalSales,
        BigDecimal totalCredits,
        BigDecimal turnover,
        BigDecimal collections,
        List<ContractTotal> byContracts
) {
    /**
     * Total d'un contrat pour la période.
     *
     * @param contractName nom du contrat, "Sans contrat" si la facture n'est rattachée à aucun
     * @param total        somme des factures de vente créées sur la période pour ce contrat
     */
    public record ContractTotal(String contractName, BigDecimal total) {}
}
