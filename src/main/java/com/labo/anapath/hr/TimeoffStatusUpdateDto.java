package com.labo.anapath.hr;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeoffStatusUpdateDto {
    @NotNull(message = "Le statut est obligatoire")
    private TimeoffStatus status;
}
