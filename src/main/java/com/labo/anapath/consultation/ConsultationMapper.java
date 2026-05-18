package com.labo.anapath.consultation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsultationMapper {

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientFirstName", source = "patient.firstname")
    @Mapping(target = "patientLastName", source = "patient.lastname")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorLastName", source = "doctor.name")
    @Mapping(target = "typeConsultationId", source = "typeConsultation.id")
    @Mapping(target = "typeConsultationName", source = "typeConsultation.name")
    @Mapping(target = "prestationId", source = "prestation.id")
    @Mapping(target = "prestationName", source = "prestation.name")
    @Mapping(target = "attribuateDoctorId", source = "attribuateDoctor.id")
    @Mapping(target = "attribuateDoctorName", expression = "java(consultation.getAttribuateDoctor() != null ? consultation.getAttribuateDoctor().getFirstname() + \" \" + consultation.getAttribuateDoctor().getLastname() : null)")
    ConsultationResponseDto toResponseDto(Consultation consultation);
}
