package com.labo.anapath.inventory;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ArticleRequestDto {

    @NotBlank(message = "Le nom de l'article est obligatoire")
    private String name;

    private String code;
    private String unit;
    private BigDecimal purchasePrice;
    private BigDecimal minimumStock;
    private BigDecimal initialQuantity;
    private UUID supplierId;
}
