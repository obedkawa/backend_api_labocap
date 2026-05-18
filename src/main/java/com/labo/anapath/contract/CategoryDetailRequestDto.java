package com.labo.anapath.contract;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CategoryDetailRequestDto {

    @NotNull
    private UUID categoryTestId;

    @NotNull
    private BigDecimal discount;
}
