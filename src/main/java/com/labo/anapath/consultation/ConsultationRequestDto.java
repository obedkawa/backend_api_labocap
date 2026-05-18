package com.labo.anapath.consultation;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ConsultationRequestDto {

    @NotNull(message = "Le patient est obligatoire")
    private UUID patientId;

    @NotNull(message = "La prestation est obligatoire")
    private UUID prestationId;

    @NotNull(message = "La date est obligatoire")
    private LocalDateTime date;

    private UUID doctorId;
    private UUID typeConsultationId;
    private String notes;
    private String motif;
    private String anamnese;
    private String antecedent;
    private String examenPhysique;
    private String diagnostic;
    private String status;
    private String paymentMode;
    private LocalDateTime nextAppointment;
}
