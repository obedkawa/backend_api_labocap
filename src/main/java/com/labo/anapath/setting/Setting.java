package com.labo.anapath.setting;

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
 * Entité représentant un paramètre de configuration applicative du laboratoire.
 * <p>
 * Les paramètres sont stockés sous forme de paires clé/valeur et peuvent inclure
 * des informations telles que le nom du labo, le logo, l'adresse ou les préfixes
 * de codes. Chaque paramètre est rattaché à une filiale.
 * La suppression est logique via le champ {@code deleted_at}.
 * </p>
 */
@Entity
@Table(name = "settings")
@SQLDelete(sql = "UPDATE settings SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Setting extends AuditableEntity {

    @Column(name = "key", nullable = false, length = 100)
    private String key;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "placeholder", length = 200)
    private String placeholder;

    @Column(name = "ico", length = 100)
    private String ico;
}
