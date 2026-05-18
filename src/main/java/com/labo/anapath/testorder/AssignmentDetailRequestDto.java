package com.labo.anapath.testorder;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AssignmentDetailRequestDto {
    @NotNull(message = "L'identifiant du bon d'examen est obligatoire")
    private UUID testOrderId;
    private String note;
    private LocalDate date;
}
