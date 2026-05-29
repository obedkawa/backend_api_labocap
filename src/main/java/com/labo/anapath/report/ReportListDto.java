package com.labo.anapath.report;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO d'une ligne du tableau "Tous les comptes rendu".
 * Réplique Laravel : Code (test_order) | Code Patient | Nom Patient | Téléphone | Date | Statut | Actions
 */
public record ReportListDto(
        UUID id,
        String reportCode,
        UUID testOrderId,
        String testOrderCode,
        UUID patientId,
        String patientCode,
        String patientFirstname,
        String patientLastname,
        String patientPhone,
        String typeOrderTitle,
        ReportStatus status,
        Boolean isDelivered,
        Boolean isCalled,
        LocalDateTime signatureDate,
        LocalDateTime createdAt
) {}
