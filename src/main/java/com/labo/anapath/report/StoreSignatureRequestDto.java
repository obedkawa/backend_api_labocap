package com.labo.anapath.report;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreSignatureRequestDto {

    @NotBlank(message = "Le nom du signataire est obligatoire")
    private String signatorName;

    @NotBlank(message = "La signature est obligatoire")
    private String signature;
}
