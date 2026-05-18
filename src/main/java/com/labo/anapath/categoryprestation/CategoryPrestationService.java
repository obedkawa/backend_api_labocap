package com.labo.anapath.categoryprestation;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface CategoryPrestationService {

    PageResponse<CategoryPrestationResponseDto> findAll(int page, int size, UUID branchId);

    CategoryPrestationResponseDto findById(UUID id);

    CategoryPrestationResponseDto create(CategoryPrestationRequestDto dto, UUID branchId);

    CategoryPrestationResponseDto update(UUID id, CategoryPrestationRequestDto dto);

    void delete(UUID id);
}
