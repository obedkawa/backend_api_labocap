package com.labo.anapath.finance;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CashboxVoucherRequestDto {

    @NotBlank
    private String description;

    private UUID supplierId;
    private UUID expenseCategoryId;
    private UUID cashboxId;
    private String ticketFile;
}
