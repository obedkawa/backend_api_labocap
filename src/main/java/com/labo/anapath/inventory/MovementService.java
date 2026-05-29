package com.labo.anapath.inventory;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface MovementService {
    PageResponse<MovementResponseDto> findAll(int page, int size, UUID branchId);
    PageResponse<MovementResponseDto> findAll(int page, int size, UUID branchId, UUID articleId);
    MovementResponseDto create(MovementRequestDto dto, UUID branchId, UUID userId);
}
