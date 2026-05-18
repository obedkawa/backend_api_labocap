package com.labo.anapath.consultation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultationDoctorUpdateDto {

    private String motif;
    private String anamnese;
    private String examenPhysique;
    private String diagnostic;
    private String antecedent;
}
