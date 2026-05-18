package com.labo.anapath.finance;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashboxRequestDto {

    @NotBlank(message = "Le nom de la caisse est obligatoire")
    private String name;

    @NotBlank(message = "Le type de caisse est obligatoire")
    private String type;
}
