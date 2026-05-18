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
 * <p>Contient les informations nécessaires pour émettre une facture :
 * référence du bon d'examen, patient, date d'échéance et liste des lignes
 * de détail (analyses facturées avec quantité et prix unitaire).</p>
 */
@Getter
@Setter
public class InvoiceRequestDto {

    /** Identifiant du bon d'examen (TestOrder) à facturer. Obligatoire. */
    @NotNull(message = "Le bon d'examen est obligatoire")
    private UUID testOrderId;

    /** Identifiant du patient facturé. Obligatoire. */
    @NotNull(message = "Le patient est obligatoire")
    private UUID patientId;

    /** Date limite de règlement. Null : paiement immédiat attendu. */
    private LocalDate dueDate;

    /** Lignes de détail de la facture (une par analyse). */
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
