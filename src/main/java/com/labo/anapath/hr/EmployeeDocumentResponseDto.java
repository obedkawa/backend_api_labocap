package com.labo.anapath.hr;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeDocumentResponseDto(
        UUID id,
        UUID employeeId,
        String name,
        String type,
        @JsonIgnore String filePath,
        Long fileSize,
        UUID branchId,
        LocalDateTime createdAt
) {}
