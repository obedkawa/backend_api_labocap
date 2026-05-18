package com.labo.anapath.inventory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de requête pour l'enregistrement d'un mouvement de stock.
 */
@Getter
@Setter
public class MovementRequestDto {

    @NotNull(message = "L'article est obligatoire")
    private UUID articleId;

    @NotNull(message = "Le type de mouvement est obligatoire")
    private MovementType type;

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité doit être positive")
    private BigDecimal quantity;

    private String notes;
}
