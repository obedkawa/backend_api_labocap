package com.labo.anapath.prestation;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PrestationRequestDto {

    @NotBlank(message = "Le nom de la prestation est obligatoire")
    private String name;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix doit être positif ou nul")
    private BigDecimal price;

    private String description;

    @NotNull(message = "La catégorie est obligatoire")
    private UUID categoryPrestationId;
}
