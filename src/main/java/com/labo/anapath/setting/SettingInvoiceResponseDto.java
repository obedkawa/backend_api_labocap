package com.labo.anapath.setting;

import java.time.LocalDateTime;
import java.util.UUID;

public record SettingInvoiceResponseDto(
        UUID id,
        String ifu,
        String token,
        Boolean status,
        UUID branchId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
