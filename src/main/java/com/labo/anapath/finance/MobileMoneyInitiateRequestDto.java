package com.labo.anapath.finance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MobileMoneyInitiateRequestDto {

    @NotNull
    private UUID invoiceId;

    @NotBlank
    private String phone;

    @NotBlank
    private String amount;

    @NotBlank
    private String provider;

    private String fee = "0";
}
