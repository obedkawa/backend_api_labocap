package com.labo.anapath.finance;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExpenseCategoryResponseDto(
        UUID id,
        String name,
        String description,
        UUID branchId,
        LocalDateTime createdAt
) {}
