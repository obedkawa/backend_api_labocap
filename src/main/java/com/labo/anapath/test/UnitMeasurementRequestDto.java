package com.labo.anapath.test;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création et la mise à jour d'une unité de mesure.
 */
@Getter
@Setter
public class UnitMeasurementRequestDto {

    /** Nom complet de l'unité de mesure (obligatoire, ex. : "milligramme par litre"). */
    @NotBlank(message = "Le nom de l'unité est obligatoire")
    private String name;

    /** Abréviation standard de l'unité (optionnelle, ex. : "mg/L"). */
    private String abbreviation;
}
