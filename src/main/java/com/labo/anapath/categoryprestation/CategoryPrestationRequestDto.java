package com.labo.anapath.categoryprestation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryPrestationRequestDto {

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    private String name;
}
