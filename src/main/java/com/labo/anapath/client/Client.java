package com.labo.anapath.client;

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
 * Entité représentant un client institutionnel du laboratoire (hôpital, clinique,
 * compagnie d'assurance, entreprise, etc.).
 * <p>
 * Un client est distinct d'un patient : il s'agit d'une organisation qui passe
 * des commandes d'examens pour le compte de ses propres patients ou assurés.
 * Chaque client est rattaché à une agence via {@code branchId} (hérité de
 * {@link com.labo.anapath.common.audit.AuditableEntity}). La suppression est
 * logique (soft delete via {@code deletedAt}).
 * </p>
 */
@Entity
@Table(name = "clients")
@SQLDelete(sql = "UPDATE clients SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Client extends AuditableEntity {

    @Column(name = "ifu", unique = true)
    private String ifu;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "adress")
    private String adress;

    @Column(name = "contact")
    private String contact;
}
