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
 * Entité représentant une unité de mesure des résultats d'analyses.
 *
 * <p>Les unités de mesure sont associées aux analyses ({@link LabTest}) pour
 * qualifier leurs résultats (ex. : mg/L, mmol/L, g/dL, UI/L).</p>
 *
 * <p>La suppression est logique (soft-delete via {@code deleted_at}).
 * Une unité ne peut être supprimée si des analyses l'utilisent.</p>
 */
@Entity
@Table(name = "unit_measurements")
@SQLDelete(sql = "UPDATE unit_measurements SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class UnitMeasurement extends AuditableEntity {

    /** Nom complet de l'unité de mesure (ex. : "milligramme par litre"). */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** Abréviation standard de l'unité (ex. : "mg/L", "mmol/L"). */
    @Column(name = "abbreviation", length = 20)
    private String abbreviation;
}
