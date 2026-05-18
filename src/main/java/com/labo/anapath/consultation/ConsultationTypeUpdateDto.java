package com.labo.anapath.consultation;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ConsultationTypeUpdateDto {

    @NotNull(message = "Le type de consultation est obligatoire")
    private UUID typeConsultationId;
}
