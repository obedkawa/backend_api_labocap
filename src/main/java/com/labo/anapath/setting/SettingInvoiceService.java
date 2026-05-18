package com.labo.anapath.setting;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface SettingInvoiceService {
    PageResponse<SettingInvoiceResponseDto> findAll(int page, int size, UUID branchId);
    SettingInvoiceResponseDto findById(UUID id);
    SettingInvoiceResponseDto update(UUID id, SettingInvoiceRequestDto dto);
    void delete(UUID id);
}
