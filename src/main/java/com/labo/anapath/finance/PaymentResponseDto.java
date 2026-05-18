package com.labo.anapath.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse exposant les informations d'un paiement enregistré.
 *
 * @param id          identifiant unique du paiement
 * @param invoiceId   identifiant de la facture réglée
 * @param amount      montant encaissé
 * @param method      mode de paiement utilisé
 * @param paymentDate date effective du règlement
 * @param notes       notes complémentaires (référence transaction, etc.)
 * @param branchId    identifiant de l'agence ayant enregistré le paiement
 * @param createdAt   date et heure de création de l'enregistrement
 */
public record PaymentResponseDto(
        UUID id,
        UUID invoiceId,
        BigDecimal amount,
        PaymentMethod method,
        LocalDate paymentDate,
        String notes,
        UUID branchId,
        LocalDateTime createdAt
) {}
