package com.labo.anapath.patient;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO agrégé représentant le profil complet d'un patient.
 * <p>
 * Combine les informations personnelles du patient avec un résumé statistique
 * de ses demandes d'examen (total, en attente, terminées) et de ses factures
 * (montants total, payé, impayé). Construit par {@link PatientServiceImpl#getProfile(java.util.UUID)}.
 * </p>
 *
 * @param patient         données personnelles du patient
 * @param totalOrders     nombre total de demandes d'examen
 * @param pendingOrders   nombre de demandes en attente de traitement
 * @param completedOrders nombre de demandes validées ou livrées
 * @param recentOrders    liste ordonnée des demandes (de la plus récente à la plus ancienne)
 * @param totalInvoiced   montant total facturé au patient
 * @param totalPaid       montant total payé par le patient
 * @param totalUnpaid     montant restant dû (totalInvoiced - totalPaid)
 * @param recentInvoices  liste ordonnée des factures (de la plus récente à la plus ancienne)
 */
public record PatientProfileDto(
        PatientResponseDto patient,
        int totalOrders,
        int pendingOrders,
        int completedOrders,
        List<TestOrderSummaryDto> recentOrders,
        BigDecimal totalInvoiced,
        BigDecimal totalPaid,
        BigDecimal totalUnpaid,
        List<InvoiceSummaryDto> recentInvoices
) {}
