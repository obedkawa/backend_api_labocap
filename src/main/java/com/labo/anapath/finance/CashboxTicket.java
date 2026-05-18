package com.labo.anapath.finance;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entité représentant un ticket de caisse (mouvement unitaire sur une caisse).
 *
 * <p>Chaque encaissement ou décaissement génère un ticket. Le champ {@code type}
 * distingue les entrées ({@code CREDIT}) des sorties ({@code DEBIT}).
 * Un ticket peut être lié à un {@link Payment} lorsqu'il résulte d'un paiement
 * de facture ; il peut aussi correspondre à une dépense diverse (fournitures, etc.).</p>
 *
 * <p>Ces tickets servent également de base pour l'intégration MECeF
 * (reçus fiscaux électroniques, API impots.bj).</p>
 */
@Entity
@Table(name = "cashbox_tickets")
@Getter
@Setter
@NoArgsConstructor
public class CashboxTicket extends AuditableEntity {

    /** Caisse sur laquelle le mouvement a été enregistré. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashbox_id", nullable = false)
    private Cashbox cashbox;

    /** Description du mouvement (libellé imprimé sur le ticket). */
    @Column(name = "label", nullable = false, length = 300)
    private String label;

    /** Montant du mouvement (toujours positif ; le sens est indiqué par {@code type}). */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    /** Sens du mouvement : {@code CREDIT} (entrée) ou {@code DEBIT} (sortie). */
    @Column(name = "type", nullable = false, length = 10)
    private String type;

    /**
     * Paiement de facture à l'origine de ce ticket.
     * Null si le ticket correspond à une dépense diverse non liée à une facture.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
