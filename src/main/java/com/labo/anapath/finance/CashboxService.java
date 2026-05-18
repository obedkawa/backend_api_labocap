package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface CashboxService {

    PageResponse<CashboxResponseDto> findAll(int page, int size, UUID branchId);

    CashboxResponseDto findById(UUID id);

    CashboxResponseDto create(CashboxRequestDto dto, UUID branchId);

    CashboxResponseDto update(UUID id, CashboxRequestDto dto);

    void delete(UUID id);
}
