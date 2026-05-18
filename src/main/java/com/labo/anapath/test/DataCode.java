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
 * Entité représentant un code de référence du catalogue du laboratoire.
 *
 * <p>Les DataCodes sont utilisés pour stocker des valeurs de référence normalisées,
 * des codes SNOMED, des terminologies ou tout autre référentiel de codification
 * utilisé dans les comptes-rendus anatomopathologiques.</p>
 *
 * <p>Le champ {@code type} permet de catégoriser les codes (ex. : "SNOMED",
 * "valeur_normale", "morphologie").</p>
 *
 * <p>La suppression est logique (soft-delete via {@code deleted_at}).</p>
 */
@Entity
@Table(name = "data_codes")
@SQLDelete(sql = "UPDATE data_codes SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class DataCode extends AuditableEntity {

    /** Code technique de référence (ex. : code SNOMED, code CIM). */
    @Column(name = "code", length = 50)
    private String code;

    /** Libellé lisible du code (obligatoire). */
    @Column(name = "label", length = 255, nullable = false)
    private String label;

    /** Type de code permettant de regrouper les codes par nature (ex. : "SNOMED"). */
    @Column(name = "type", length = 100)
    private String type;
}
