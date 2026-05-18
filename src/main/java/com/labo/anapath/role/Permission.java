package com.labo.anapath.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant une permission atomique du système RBAC.
 *
 * <p>Une permission identifie une action autorisée sur une ressource.
 * Le slug suit la convention {@code {operation}-{ressource}} (ex. : {@code view-patients},
 * {@code create-test-orders}). Ces slugs sont embarqués dans le JWT à l'authentification
 * pour permettre une vérification stateless des droits.</p>
 *
 * <p>Contrairement aux rôles, les permissions ne sont pas gérées par succursale :
 * elles sont globales et pré-enregistrées en base par les scripts de seeding.</p>
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Permission {

    /** Identifiant unique généré automatiquement par UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Libellé lisible de la permission (ex. : "Voir les patients"). */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Slug technique unique utilisé dans les vérifications de sécurité Spring.
     * Format : {@code {operation}-{ressource}} (ex. : {@code manage-users}).
     */
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    /** Date et heure de création de la permission (gérée automatiquement par l'audit JPA). */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
