package com.labo.anapath.hr;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeDocumentResponseDto(
        UUID id,
        UUID employeeId,
        String name,
        String type,
        String filePath,
        Long fileSize,
        UUID branchId,
        LocalDateTime createdAt
) {}
