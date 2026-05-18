package com.labo.anapath.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SignalRequestDto {
    @NotNull
    private UUID testOrderId;
    @NotBlank
    private String typeSignal;
    private String commentaire;
}
