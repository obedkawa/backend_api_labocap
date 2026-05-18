package com.labo.anapath.doctor;

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
 * Entité représentant un hôpital ou une structure sanitaire référencée dans le système.
 * <p>
 * Les hôpitaux sont associés à une agence ({@code branchId}) et servent de référentiel
 * pour identifier l'établissement d'où proviennent les prescriptions médicales.
 * Le champ {@code commission} représente le taux de commission éventuelle accordé
 * à l'établissement. La suppression est logique (soft delete).
 * </p>
 */
@Entity
@Table(name = "hospitals")
@SQLDelete(sql = "UPDATE hospitals SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Hospital extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "adresse", columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "commission")
    private Double commission;
}
