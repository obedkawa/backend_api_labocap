package com.labo.anapath.setting;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SettingInvoiceMapper {
    SettingInvoiceResponseDto toResponseDto(SettingInvoice settingInvoice);
}
