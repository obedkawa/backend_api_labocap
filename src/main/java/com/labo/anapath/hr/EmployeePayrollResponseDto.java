package com.labo.anapath.hr;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeePayrollResponseDto(
        UUID id,
        UUID employeeId,
        int month,
        int year,
        BigDecimal grossSalary,
        BigDecimal deductions,
        BigDecimal netSalary,
        LocalDate paidAt,
        LocalDateTime createdAt
) {}
