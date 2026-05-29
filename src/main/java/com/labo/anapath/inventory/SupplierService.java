package com.labo.anapath.inventory;

import com.labo.anapath.common.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface SupplierService {
    PageResponse<SupplierResponseDto> findAll(int page, int size, UUID branchId);
    List<SupplierResponseDto> search(String q, UUID branchId);
    SupplierResponseDto findById(UUID id);
    SupplierResponseDto create(SupplierRequestDto dto, UUID branchId);
    SupplierResponseDto update(UUID id, SupplierRequestDto dto);
    void delete(UUID id);
}
