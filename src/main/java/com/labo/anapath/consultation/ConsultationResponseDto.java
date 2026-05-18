package com.labo.anapath.consultation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultationResponseDto(
        UUID id,
        String code,
        UUID patientId,
        String patientFirstName,
        String patientLastName,
        UUID doctorId,
        String doctorLastName,
        UUID typeConsultationId,
        String typeConsultationName,
        UUID prestationId,
        String prestationName,
        UUID attribuateDoctorId,
        String attribuateDoctorName,
        String notes,
        String motif,
        String anamnese,
        String antecedent,
        String examenPhysique,
        String diagnostic,
        BigDecimal fees,
        String status,
        String paymentMode,
        LocalDateTime date,
        LocalDateTime nextAppointment,
        UUID branchId,
        LocalDateTime createdAt
) {}
