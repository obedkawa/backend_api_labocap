package com.labo.anapath.finance;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceStatusUpdateDto {

    @NotBlank
    private String payment;
}
