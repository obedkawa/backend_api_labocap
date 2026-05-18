package com.labo.anapath.support;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface SignalService {
    PageResponse<SignalResponseDto> findAll(int page, int size, UUID branchId);
    SignalResponseDto create(SignalRequestDto dto, UUID userId, UUID branchId);
}
