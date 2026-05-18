package com.labo.anapath.prestationorder;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PrestationOrderRequestDto {

    @NotNull(message = "Le patient est obligatoire")
    private UUID patientId;

    @NotNull(message = "La prestation est obligatoire")
    private UUID prestationId;

    private String status;
}
