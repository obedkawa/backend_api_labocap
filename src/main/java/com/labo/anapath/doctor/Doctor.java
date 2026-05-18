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
 * Entité représentant un médecin prescripteur référencé dans le système.
 * <p>
 * Les médecins sont associés à une agence ({@code branchId}) et peuvent être
 * liés à un hôpital via les demandes d'examen (TestOrder). Le champ
 * {@code commission} représente le taux de commission éventuelle accordé au
 * médecin prescripteur. La suppression est logique (soft delete).
 * </p>
 */
@Entity
@Table(name = "doctors")
@SQLDelete(sql = "UPDATE doctors SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Doctor extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "role", length = 100)
    private String role;

    @Column(name = "commission")
    private Double commission;
}
