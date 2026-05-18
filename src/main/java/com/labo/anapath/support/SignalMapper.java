package com.labo.anapath.support;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SignalMapper {

    @Mapping(target = "testOrderId", source = "testOrder.id")
    @Mapping(target = "userId", source = "user.id")
    SignalResponseDto toResponseDto(Signal signal);
}
