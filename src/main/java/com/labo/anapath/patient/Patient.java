package com.labo.anapath.patient;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

/**
 * Entité représentant le dossier d'un patient du laboratoire d'anatomopathologie.
 * <p>
 * Chaque patient est rattaché à une agence ({@code branchId}). Le code patient
 * est unique au sein d'une agence et sert de référence métier. L'âge peut être
 * exprimé en années ou en mois selon le champ {@code yearOrMonth} (pertinent pour
 * les nourrissons). La date de naissance est facultative et complémentaire à l'âge.
 * La suppression est logique (soft delete via {@code deletedAt}).
 * </p>
 */
@Entity
@Table(name = "patients")
@SQLDelete(sql = "UPDATE patients SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Patient extends AuditableEntity {

    @Column(name = "code", unique = true, length = 100)
    private String code;

    @Column(name = "firstname", nullable = false, length = 200)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 200)
    private String lastname;

    @Column(name = "genre", length = 20)
    private String genre;

    @Column(name = "telephone1", length = 20)
    private String telephone1;

    @Column(name = "telephone2", length = 20)
    private String telephone2;

    @Column(name = "adresse", columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "age")
    private Integer age;

    @Column(name = "year_or_month")
    private Boolean yearOrMonth;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "profession", length = 200)
    private String profession;

    @Column(name = "langue", length = 20)
    private String langue;

    @Column(name = "email", length = 100)
    private String email;
}
