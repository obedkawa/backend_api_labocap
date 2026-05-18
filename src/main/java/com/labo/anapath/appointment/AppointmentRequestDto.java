package com.labo.anapath.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AppointmentRequestDto {

    @NotNull
    private UUID patientId;

    private UUID doctorId;

    @NotNull
    private LocalDateTime time;

    private String message;

    private String priority;
}
