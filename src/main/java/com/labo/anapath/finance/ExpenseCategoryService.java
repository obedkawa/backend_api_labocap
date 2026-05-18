package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface ExpenseCategoryService {

    PageResponse<ExpenseCategoryResponseDto> findAll(int page, int size, UUID branchId);

    ExpenseCategoryResponseDto findById(UUID id);

    ExpenseCategoryResponseDto create(ExpenseCategoryRequestDto dto, UUID branchId);

    ExpenseCategoryResponseDto update(UUID id, ExpenseCategoryRequestDto dto);

    void delete(UUID id);
}
