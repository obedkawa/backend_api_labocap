package com.labo.anapath.finance;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundRequestStatusUpdateDto {

    @NotBlank(message = "Le statut est obligatoire")
    private String status;
}
