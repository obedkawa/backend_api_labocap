package com.labo.anapath.hr;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeTimeoffRequestDto {
    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;
    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate endDate;
    private String reason;
}
