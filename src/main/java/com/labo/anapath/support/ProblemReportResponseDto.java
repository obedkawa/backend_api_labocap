package com.labo.anapath.support;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProblemReportResponseDto(
        UUID id,
        UUID testOrderId,
        UUID problemCategoryId,
        String problemCategoryName,
        String description,
        String status,
        UUID branchId,
        LocalDateTime createdAt
) {}
