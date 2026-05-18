package com.labo.anapath.prestationorder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PrestationOrderResponseDto(
        UUID id,
        UUID patientId,
        String patientName,
        UUID prestationId,
        String prestationName,
        BigDecimal total,
        String status,
        UUID branchId,
        LocalDateTime createdAt
) {}
