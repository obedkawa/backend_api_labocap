package com.labo.anapath.testorder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentResponseDto(
        UUID id,
        String code,
        UUID userId,
        String userName,
        LocalDate date,
        String note,
        int nbrDetails,
        UUID branchId,
        LocalDateTime createdAt
) {}
