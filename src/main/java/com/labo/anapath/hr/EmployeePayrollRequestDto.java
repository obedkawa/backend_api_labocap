package com.labo.anapath.hr;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeePayrollRequestDto {
    @NotNull(message = "Le mois est obligatoire")
    private Integer month;
    @NotNull(message = "L'année est obligatoire")
    private Integer year;
    @NotNull(message = "Le salaire brut est obligatoire")
    private BigDecimal grossSalary;
    private BigDecimal deductions = BigDecimal.ZERO;
    private LocalDate paidAt;
}
