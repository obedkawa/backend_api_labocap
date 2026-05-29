package com.labo.anapath.hr;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeContratRequestDto {
    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    @NotNull(message = "Le salaire est obligatoire")
    private BigDecimal salary;
}
