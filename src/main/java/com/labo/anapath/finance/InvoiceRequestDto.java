package com.labo.anapath.finance;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO de création / mise à jour d'une facture.
 *
 * <p>Comportement aligné sur Laravel : seul {@code testOrderId} est obligatoire.
 * Tous les autres champs sont optionnels — le backend dérive automatiquement le
 * patient, les montants et les lignes de détail depuis le bon d'examen ciblé,
 * et génère le code facture (format {@code FAYYNNNN}).</p>
 */
@Getter
@Setter
public class InvoiceRequestDto {

    /** Identifiant du bon d'examen (TestOrder) à facturer. Obligatoire. */
    @NotNull(message = "Le bon d'examen est obligatoire")
    private UUID testOrderId;

    /** Date de la facture (informatif). Si null : date du jour côté serveur. */
    private LocalDate date;

    /**
     * Identifiant du patient facturé. Optionnel — sinon récupéré depuis le bon d'examen
     * (comportement Laravel).
     */
    private UUID patientId;

    /** Date limite de règlement. Null : paiement immédiat attendu. */
    private LocalDate dueDate;

    /**
     * Lignes de détail de la facture. Optionnel — si vide, les lignes sont générées
     * automatiquement à partir des analyses du bon d'examen.
     */
    private List<InvoiceDetailRequestDto> details = new ArrayList<>();

    /**
     * DTO imbriqué représentant une ligne de facture lors de la création.
     */
    @Getter
    @Setter
    public static class InvoiceDetailRequestDto {
        /** Identifiant de l'analyse (LabTest) facturée sur cette ligne. */
        private UUID labTestId;
        /** Quantité facturée (1 par défaut). */
        private int quantity = 1;
        /** Prix unitaire après remise et application éventuelle du contrat. */
        private BigDecimal unitPrice;
    }
}
