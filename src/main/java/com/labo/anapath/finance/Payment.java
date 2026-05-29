package com.labo.anapath.finance;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entité représentant un encaissement (paiement) effectué sur une facture.
 *
 * <p>Plusieurs paiements peuvent être liés à une même facture, ce qui permet
 * de gérer les règlements partiels (statut {@link InvoiceStatus#PARTIALLY_PAID})
 * ainsi que les paiements mixtes (ex. acompte en espèces + solde Mobile Money).</p>
 *
 * <p>Les paiements Mobile Money transitent par l'API Sckaler avec le préfixe
 * Bénin «&nbsp;229&nbsp;» pour les opérateurs MTN et MOOV.</p>
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends AuditableEntity {

    /** Facture réglée par ce paiement. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    /** Montant encaissé lors de cette opération. */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    /** Mode de paiement utilisé (espèces par défaut). */
    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 30)
    private PaymentMethod method = PaymentMethod.CASH;

    /** Date effective du paiement (peut différer de la date d'enregistrement). */
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    /** Notes libres : référence de transaction Mobile Money, numéro de virement, etc. */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /** Provider Mobile Money : "MOBILEMONEY-MTN" ou "MOBILEMONEY-MOOV". */
    @Column(name = "payment_name", length = 50)
    private String paymentName;

    /** Numéro de téléphone Mobile Money normalisé avec préfixe pays. */
    @Column(name = "payment_number", columnDefinition = "TEXT")
    private String paymentNumber;

    /** Statut du paiement Sckaler : INITIATED, SUCCESS, FAILED, PENDING. */
    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    /** Montant en string tel qu'envoyé à Sckaler. */
    @Column(name = "payment_amount", length = 20)
    private String paymentAmount;

    /** Identifiant de transaction retourné par Sckaler. */
    @Column(name = "payment_id", length = 100)
    private String paymentId;

    /** Description envoyée à Sckaler (code examen + frais). */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
