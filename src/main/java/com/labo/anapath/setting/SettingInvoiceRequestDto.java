package com.labo.anapath.setting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingInvoiceRequestDto {
    @NotBlank(message = "L'IFU est obligatoire")
    @Size(max = 100, message = "L'IFU ne peut pas dépasser 100 caractères")
    private String ifu;

    @NotBlank(message = "Le token est obligatoire")
    @Size(max = 500, message = "Le token ne peut pas dépasser 500 caractères")
    private String token;

    private Boolean status;
}
