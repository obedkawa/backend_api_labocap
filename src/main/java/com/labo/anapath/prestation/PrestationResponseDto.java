package com.labo.anapath.prestation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PrestationResponseDto(
        UUID id,
        String name,
        BigDecimal price,
        String description,
        UUID categoryPrestationId,
        String categoryPrestationName,
        UUID branchId,
        LocalDateTime createdAt
) {}
