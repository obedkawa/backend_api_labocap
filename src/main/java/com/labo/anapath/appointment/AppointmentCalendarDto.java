package com.labo.anapath.appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentCalendarDto(
        UUID id,
        String title,
        LocalDateTime start,
        UUID doctorId,
        String doctorName,
        String className
) {}
