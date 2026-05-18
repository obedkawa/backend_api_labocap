package com.labo.anapath.doc;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocVersionMapper {

    @Mapping(target = "docId", source = "doc.id")
    @Mapping(target = "userId", source = "user.id")
    DocVersionResponseDto toResponseDto(DocVersion version);
}
