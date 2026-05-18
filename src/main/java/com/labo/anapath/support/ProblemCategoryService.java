package com.labo.anapath.support;

import java.util.List;
import java.util.UUID;

public interface ProblemCategoryService {
    List<ProblemCategoryResponseDto> findAll(UUID branchId);
    ProblemCategoryResponseDto findById(UUID id);
    ProblemCategoryResponseDto create(ProblemCategoryRequestDto dto, UUID branchId);
    ProblemCategoryResponseDto update(UUID id, ProblemCategoryRequestDto dto);
    void delete(UUID id);
}
