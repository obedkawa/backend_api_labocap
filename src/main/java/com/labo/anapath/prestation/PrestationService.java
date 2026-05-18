package com.labo.anapath.prestation;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface PrestationService {

    PageResponse<PrestationResponseDto> findAll(int page, int size, UUID branchId, UUID categoryId);

    PrestationResponseDto findById(UUID id);

    PrestationResponseDto create(PrestationRequestDto dto, UUID branchId);

    PrestationResponseDto update(UUID id, PrestationRequestDto dto);

    void delete(UUID id);
}
