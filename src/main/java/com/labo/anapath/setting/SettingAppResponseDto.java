package com.labo.anapath.setting;

import java.util.UUID;

public record SettingAppResponseDto(
        UUID id,
        String key,
        String value,
        String label,
        UUID branchId
) {}
