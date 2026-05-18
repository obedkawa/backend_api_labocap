package com.labo.anapath.doc;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentationCategoryMapper {
    DocumentationCategoryResponseDto toResponseDto(DocumentationCategory category);
}
