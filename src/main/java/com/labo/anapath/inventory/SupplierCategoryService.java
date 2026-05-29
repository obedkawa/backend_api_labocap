package com.labo.anapath.inventory;

import java.util.List;
import java.util.UUID;

public interface SupplierCategoryService {
    List<SupplierCategoryResponseDto> findAll(UUID branchId);
    SupplierCategoryResponseDto create(SupplierCategoryRequestDto dto, UUID branchId);
    SupplierCategoryResponseDto update(UUID id, SupplierCategoryRequestDto dto);
    void delete(UUID id);
}
