package com.labo.anapath.finance;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "cashboxes")
@SQLDelete(sql = "UPDATE cashboxes SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Cashbox extends AuditableEntity {

    /** Nom de la caisse (ex. «Caisse principale Cotonou»). */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** Solde courant de la caisse. Mis à jour à chaque mouvement. */
    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /** Type de caisse : "vente" (encaissements) ou "depense" (décaissements). */
    @Column(name = "type", nullable = false, length = 20)
    private String type = "vente";

    /** Solde d'ouverture de la session journalière en cours. */
    @Column(name = "opening_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal openingBalance = BigDecimal.ZERO;

    /** Statut de la caisse : 0 = fermée, 1 = ouverte en cours de journée. */
    @Column(name = "statut", nullable = false)
    private Integer statut = 0;
}
