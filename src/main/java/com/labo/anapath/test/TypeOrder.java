package com.labo.anapath.test;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entité représentant un type de bon de demande d'analyses (TypeOrder).
 *
 * <p>Un type de bon caractérise la nature de la demande anatomopathologique :
 * biopsie, cytologie, extemporané, pièce opératoire, etc.
 * Il est identifié par un titre lisible et un slug technique unique.</p>
 *
 * <p>La suppression est logique (soft-delete via {@code deleted_at}).</p>
 */
@Entity
@Table(name = "type_orders")
@SQLDelete(sql = "UPDATE type_orders SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class TypeOrder extends AuditableEntity {

    /** Titre lisible du type de bon (ex. : "Biopsie", "Cytologie"). */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * Slug technique unique identifiant le type de bon dans le code métier.
     * Fourni explicitement par l'utilisateur (non dérivé du titre).
     */
    @Column(name = "slug", length = 100, unique = true)
    private String slug;
}
