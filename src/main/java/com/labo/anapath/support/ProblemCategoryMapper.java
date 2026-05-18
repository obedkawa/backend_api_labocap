package com.labo.anapath.support;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProblemCategoryMapper {
    ProblemCategoryResponseDto toResponseDto(ProblemCategory category);
}
