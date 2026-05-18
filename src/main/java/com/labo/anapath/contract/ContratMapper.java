package com.labo.anapath.contract;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContratMapper {

    @Mapping(target = "hospitalId", source = "hospital.id")
    @Mapping(target = "hospitalName", source = "hospital.name")
    ContratResponseDto toResponseDto(Contrat contrat);

    @Mapping(target = "labTestId", source = "labTest.id")
    @Mapping(target = "labTestName", source = "labTest.name")
    DetailsContratDto toDetailsDto(DetailsContrat details);
}
