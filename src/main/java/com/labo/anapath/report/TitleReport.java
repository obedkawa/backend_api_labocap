package com.labo.anapath.report;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entité représentant un titre de section prédéfini pour les comptes-rendus anatomopathologiques.
 *
 * <p>Les titres permettent de standardiser la structure des CRs (ex. "MACROSCOPIE",
 * "MICROSCOPIE", "CONCLUSION") afin d'accélérer la rédaction et d'assurer une
 * présentation homogène des rapports. Chaque titre est isolé par branche.
 */
@Entity
@Table(name = "title_reports")
@Getter
@Setter
@NoArgsConstructor
public class TitleReport extends AuditableEntity {

    /** Libellé du titre de section (ex. "MACROSCOPIE", "CONCLUSION"). */
    @Column(name = "name", nullable = false, length = 300)
    private String name;

    /** Indique si ce titre est le titre par défaut. Un seul titre peut être par défaut à la fois. */
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;
}
