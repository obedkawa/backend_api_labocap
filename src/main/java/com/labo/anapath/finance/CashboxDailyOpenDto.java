package com.labo.anapath.finance;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CashboxDailyOpenDto {

    @NotNull
    @DecimalMin("0")
    private BigDecimal soldeOuverture;

    private UUID cashboxId;
}
