package com.labo.anapath.patient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de résumé d'une facture, utilisé dans le profil patient ({@link PatientProfileDto}).
 * <p>
 * Ne contient que les informations essentielles pour afficher l'historique de facturation
 * d'un patient sans charger l'intégralité des détails de chaque facture.
 * </p>
 *
 * @param id        identifiant unique de la facture
 * @param total     montant total de la facture
 * @param status    statut de la facture (ex. : "PAID", "PENDING", "CANCELLED")
 * @param dueDate   date d'échéance de paiement
 * @param createdAt date et heure de création de la facture
 */
public record InvoiceSummaryDto(
        UUID id,
        BigDecimal total,
        String status,
        LocalDate dueDate,
        LocalDateTime createdAt
) {}
