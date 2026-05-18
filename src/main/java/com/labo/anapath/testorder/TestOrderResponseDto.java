package com.labo.anapath.testorder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de réponse représentant un bon d'examen avec toutes ses informations dénormalisées.
 *
 * <p>Les associations (patient, médecin, hôpital, type de bon) sont aplaties
 * en champs scalaires (id + libellé) pour simplifier la consommation par l'API REST.
 * La liste {@code details} contient les analyses commandées avec leurs montants calculés.
 */
public record TestOrderResponseDto(
        /** Identifiant unique du bon d'examen. */
        UUID id,
        /** Code unique généré lors de la validation (ex. {@code EX26-0001}). Null si PENDING. */
        String code,
        /** Statut courant dans la machine d'état du bon d'examen. */
        TestOrderStatus status,
        /** Date du prélèvement anatomopathologique. */
        LocalDate prelevementDate,
        /** Référence interne de l'hôpital prescripteur. */
        String referenceHopital,
        /** Indique si le bon est marqué comme urgent. */
        Boolean isUrgent,
        /** Sous-total avant remise. */
        Double subtotal,
        /** Remise globale en valeur absolue. */
        Double discount,
        /** Montant total après remise. */
        Double total,
        /** Identifiant du patient. */
        UUID patientId,
        /** Prénom du patient. */
        String patientFirstname,
        /** Nom de famille du patient. */
        String patientLastname,
        /** Identifiant du médecin prescripteur. */
        UUID doctorId,
        /** Nom du médecin prescripteur. */
        String doctorName,
        /** Identifiant de l'hôpital d'origine. */
        UUID hospitalId,
        /** Nom de l'hôpital d'origine. */
        String hospitalName,
        /** Identifiant du contrat de facturation. */
        UUID contratId,
        /** Identifiant du type de bon d'examen. */
        UUID typeOrderId,
        /** Libellé du type de bon d'examen. */
        String typeOrderTitle,
        /** Identifiant du médecin anatomopathologiste attribué pour lecture. */
        UUID attribuateDoctorId,
        /** Identifiant de l'utilisateur/technicien assigné pour traitement. */
        UUID assignedToUserId,
        /** Liste des analyses demandées avec prix et remises individuels. */
        List<DetailTestOrderDto> details,
        /** Identifiant de la branche propriétaire (isolation multi-tenant). */
        UUID branchId,
        /** Date et heure de création du bon. */
        LocalDateTime createdAt
) {}
