package com.labo.anapath.test;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création et la mise à jour d'un code de référence.
 */
@Getter
@Setter
public class DataCodeRequestDto {

    /** Code technique optionnel (ex. : code SNOMED, code CIM). */
    private String code;

    /** Libellé lisible du code (obligatoire). */
    @NotBlank(message = "Le libellé est obligatoire")
    private String label;

    /** Type de code permettant de regrouper les entrées par nature (ex. : "SNOMED"). */
    private String type;
}
