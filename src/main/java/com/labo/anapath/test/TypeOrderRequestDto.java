package com.labo.anapath.test;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création et la mise à jour d'un type de bon de demande.
 */
@Getter
@Setter
public class TypeOrderRequestDto {

    /** Titre lisible du type de bon (obligatoire, ex. : "Biopsie"). */
    @NotBlank(message = "Le titre du type de bon est obligatoire")
    private String title;

    /**
     * Slug technique unique du type de bon (obligatoire).
     * Fourni explicitement par l'utilisateur (ex. : "biopsie", "cytologie").
     * Utilisé dans le code métier pour identifier le type de demande.
     */
    @NotBlank(message = "Le slug du type est obligatoire")
    private String slug;
}
