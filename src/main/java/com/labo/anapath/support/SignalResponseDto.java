package com.labo.anapath.support;

import java.time.LocalDateTime;
import java.util.UUID;

public record SignalResponseDto(
        UUID id,
        UUID testOrderId,
        String typeSignal,
        String commentaire,
        Boolean status,
        UUID userId,
        UUID branchId,
        LocalDateTime createdAt
) {}
