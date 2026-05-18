package com.labo.anapath.setting;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface SettingAppService {
    PageResponse<SettingAppResponseDto> findAll(int page, int size, UUID branchId);
    SettingAppResponseDto findById(UUID id);
    SettingAppResponseDto upsert(SettingAppRequestDto dto, UUID branchId);
    void delete(UUID id);
}
