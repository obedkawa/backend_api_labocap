package com.labo.anapath.prestationorder;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrestationOrderMapper {

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientName", expression = "java(order.getPatient().getFirstname() + \" \" + order.getPatient().getLastname())")
    @Mapping(target = "prestationId", source = "prestation.id")
    @Mapping(target = "prestationName", source = "prestation.name")
    PrestationOrderResponseDto toResponseDto(PrestationOrder order);
}
