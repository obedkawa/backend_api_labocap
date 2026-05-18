package com.labo.anapath.setting;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingAppRequestDto {

    @NotBlank(message = "La clé est obligatoire")
    private String key;

    private String value;
}
