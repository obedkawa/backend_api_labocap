package com.labo.anapath.prestationorder;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface PrestationOrderService {

    PageResponse<PrestationOrderResponseDto> findAll(int page, int size, UUID branchId);

    PrestationOrderResponseDto findById(UUID id);

    PrestationOrderResponseDto create(PrestationOrderRequestDto dto, UUID branchId);

    PrestationOrderResponseDto update(UUID id, PrestationOrderRequestDto dto);

    void delete(UUID id);
}
