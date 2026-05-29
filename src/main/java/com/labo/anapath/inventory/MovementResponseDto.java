package com.labo.anapath.inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovementResponseDto(
        UUID id,
        UUID articleId,
        String articleName,
        MovementType type,
        BigDecimal quantity,
        String notes,
        UUID branchId,
        LocalDateTime createdAt,
        UUID userId,
        String userFullName,
        LocalDate movementDate
) {}
