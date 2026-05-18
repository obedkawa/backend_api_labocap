package com.labo.anapath.appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponseDto(
        UUID id,
        UUID patientId,
        String patientFirstname,
        String patientLastname,
        UUID doctorId,
        String doctorFirstname,
        String doctorLastname,
        LocalDateTime date,
        String priority,
        String status,
        String message,
        UUID branchId,
        LocalDateTime createdAt
) {}
