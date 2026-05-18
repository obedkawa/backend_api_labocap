package com.labo.anapath.branch;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création ou la mise à jour d'une agence.
 * <p>
 * Le nom est le seul champ obligatoire ; le code interne et la localisation
 * sont optionnels.
 * </p>
 */
@Getter
@Setter
public class BranchRequestDto {

    /** Nom de l'agence (obligatoire, ne peut pas être vide). */
    @NotBlank(message = "Le nom de l'agence est obligatoire")
    private String name;

    /** Code interne de l'agence (optionnel). */
    private String code;

    /** Localisation géographique de l'agence (optionnel). */
    private String location;
}
