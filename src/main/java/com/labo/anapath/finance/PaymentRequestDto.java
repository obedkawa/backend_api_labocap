package com.labo.anapath.finance;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de création d'un paiement sur une facture.
 *
 * <p>Utilisé pour enregistrer un encaissement : la facture ciblée, le montant,
 * le mode de paiement et la date effective du règlement.</p>
 */
@Getter
@Setter
public class PaymentRequestDto {

    /** Identifiant de la facture à régler. Obligatoire. */
    @NotNull(message = "La facture est obligatoire")
    private UUID invoiceId;

    /** Montant encaissé (doit être strictement positif). */
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être positif")
    private BigDecimal amount;

    /** Mode de paiement utilisé. Par défaut : espèces. */
    private PaymentMethod method = PaymentMethod.CASH;

    /** Date effective du paiement (peut être antérieure à la date de saisie). */
    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDate paymentDate;

    /** Notes complémentaires (référence Mobile Money, numéro de chèque, etc.). */
    private String notes;
}
