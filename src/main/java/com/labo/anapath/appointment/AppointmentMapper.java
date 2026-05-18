package com.labo.anapath.appointment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientFirstname", source = "patient.firstname")
    @Mapping(target = "patientLastname", source = "patient.lastname")
    @Mapping(target = "doctorId", source = "doctorInterne.id")
    @Mapping(target = "doctorFirstname", source = "doctorInterne.firstname")
    @Mapping(target = "doctorLastname", source = "doctorInterne.lastname")
    @Mapping(target = "date", source = "date")
    AppointmentResponseDto toResponseDto(Appointment appointment);
}
