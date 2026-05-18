package com.labo.anapath.inventory;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierCategoryRequestDto {

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    private String name;

    private String description;
}
