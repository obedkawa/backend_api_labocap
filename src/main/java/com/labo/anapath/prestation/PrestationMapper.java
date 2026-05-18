package com.labo.anapath.prestation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrestationMapper {

    @Mapping(target = "categoryPrestationId", source = "categoryPrestation.id")
    @Mapping(target = "categoryPrestationName", source = "categoryPrestation.name")
    PrestationResponseDto toResponseDto(Prestation prestation);
}
