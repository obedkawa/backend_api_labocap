package com.labo.anapath.inventory;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ArticleRequestDto {

    @NotBlank(message = "Le nom de l'article est obligatoire")
    private String name;

    private String code;
    private String description;
    private String unit;
    private BigDecimal purchasePrice;
    private BigDecimal minimumStock;
    private BigDecimal initialQuantity;
    private String lotNumber;
    private LocalDate expirationDate;
    private UUID supplierId;
}
