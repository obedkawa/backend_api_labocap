package com.labo.anapath.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO de requête pour la création ou la mise à jour d'un dossier patient.
 * <p>
 * Le code, le prénom et le nom sont obligatoires. Le code patient doit être
 * unique au sein d'une agence. L'âge et la date de naissance sont complémentaires ;
 * {@code yearOrMonth} précise si l'âge est exprimé en années ({@code true}) ou
 * en mois ({@code false}), ce qui est utile pour les nourrissons.
 * </p>
 */
@Getter
@Setter
public class PatientRequestDto {

    /** Code patient unique au sein de l'agence (obligatoire, référence métier). */
    @NotBlank(message = "Le code patient est obligatoire")
    private String code;

    /** Prénom du patient (obligatoire). */
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstname;

    /** Nom de famille du patient (obligatoire). */
    @NotBlank(message = "Le nom est obligatoire")
    private String lastname;

    /** Genre du patient (ex. : "M", "F") — optionnel. */
    private String genre;

    /** Numéro de téléphone principal — optionnel, unique au sein de l'agence. */
    private String telephone1;

    /** Numéro de téléphone secondaire — optionnel. */
    private String telephone2;

    /** Adresse physique du patient — optionnel. */
    private String adresse;

    /** Âge du patient (en années ou en mois selon {@code yearOrMonth}) — optionnel. */
    private Integer age;

    /**
     * Indique si l'âge est exprimé en années ({@code true}) ou en mois ({@code false}).
     * Pertinent pour les nourrissons dont l'âge en années serait 0.
     */
    private Boolean yearOrMonth;

    /** Date de naissance du patient — doit être dans le passé si fournie. */
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate birthday;

    /** Profession du patient — optionnel. */
    private String profession;

    /** Langue parlée par le patient (ex. : "fr", "fon") — optionnel. */
    private String langue;

    /** Adresse e-mail du patient — optionnel, doit être valide si fournie. */
    @Email(message = "L'email doit être valide")
    private String email;
}
