package com.labo.anapath.report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReportMapper {

    @Mapping(target = "testOrderId", source = "testOrder.id")
    @Mapping(target = "testOrderCode", source = "testOrder.code")
    @Mapping(target = "titleId", source = "titleReport.id")
    @Mapping(target = "titleName", source = "titleReport.name")
    @Mapping(target = "isDelivered", source = "delivered")
    @Mapping(target = "isCalled", source = "called")
    @Mapping(target = "signatory1Id", source = "signatory1.id")
    @Mapping(target = "signatory1Name", expression = "java(report.getSignatory1() != null ? report.getSignatory1().getFirstname() + ' ' + report.getSignatory1().getLastname() : null)")
    @Mapping(target = "tagNames", expression = "java(report.getTags().stream().map(com.labo.anapath.report.Tag::getName).toList())")
    ReportResponseDto toResponseDto(Report report);
}
