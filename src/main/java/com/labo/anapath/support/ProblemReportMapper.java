package com.labo.anapath.support;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProblemReportMapper {

    @Mapping(target = "testOrderId", source = "testOrder.id")
    @Mapping(target = "problemCategoryId", source = "problemCategory.id")
    @Mapping(target = "problemCategoryName", source = "problemCategory.name")
    ProblemReportResponseDto toResponseDto(ProblemReport report);
}
