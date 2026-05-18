package com.labo.anapath.categoryprestation;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryPrestationResponseDto(
        UUID id,
        String name,
        String slug,
        UUID branchId,
        LocalDateTime createdAt
) {}
