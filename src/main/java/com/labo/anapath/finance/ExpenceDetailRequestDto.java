package com.labo.anapath.finance;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ExpenceDetailRequestDto {

    @NotBlank
    private String articleName;

    @NotNull
    @DecimalMin("1")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal unitPrice;
}
