package com.labo.anapath.setting;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création ou la mise à jour d'un paramètre de configuration.
 */
@Getter
@Setter
public class SettingRequestDto {

    @NotBlank(message = "La clé est obligatoire")
    private String key;

    private String value;
}
