package com.labo.anapath.hr;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeTimeoffResponseDto(
        UUID id,
        UUID employeeId,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        TimeoffStatus status,
        LocalDateTime createdAt
) {}
