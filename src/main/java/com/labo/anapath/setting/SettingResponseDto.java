package com.labo.anapath.setting;

import java.util.UUID;

/**
 * DTO de réponse représentant un paramètre de configuration du laboratoire.
 * Le champ {@code ico} correspond à l'icône d'interface associée à ce paramètre.
 */
public record SettingResponseDto(
        UUID id,
        String key,
        String value,
        String placeholder,
        String ico,
        UUID branchId
) {}
