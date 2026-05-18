package com.labo.anapath.report;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entité représentant un mot-clé (tag) utilisé pour classifier les comptes-rendus.
 *
 * <p>Les tags permettent de regrouper et de retrouver rapidement des CRs
 * par thématique (ex. "côlon", "sein", "peau"). Ils sont associés aux
 * {@link Report}s via une relation ManyToMany (table {@code report_tags}).
 * Chaque tag est isolé par branche.
 */
@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag extends AuditableEntity {

    /** Libellé du tag (unique par branche de facto). */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
}
