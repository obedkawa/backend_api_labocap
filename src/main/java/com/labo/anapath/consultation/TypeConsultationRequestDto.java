package com.labo.anapath.consultation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeConsultationRequestDto {
    @NotBlank
    private String name;
}
