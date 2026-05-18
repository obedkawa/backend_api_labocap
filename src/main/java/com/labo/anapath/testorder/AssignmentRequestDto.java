package com.labo.anapath.testorder;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AssignmentRequestDto {
    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    private UUID userId;
    private LocalDate date;
    private String note;
}
