package com.labo.anapath.contract;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface ContratService {

    PageResponse<ContratResponseDto> findAll(int page, int size, UUID branchId);

    ContratResponseDto findById(UUID id);

    ContratResponseDto create(ContratRequestDto dto, UUID branchId);

    ContratResponseDto update(UUID id, ContratRequestDto dto);

    void delete(UUID id);

    DetailsContratDto addCategoryDetail(UUID contractId, CategoryDetailRequestDto dto);

    DetailsContratDto addTestDetail(UUID contractId, TestDetailRequestDto dto);

    ContratResponseDto activate(UUID contractId);

    ContratResponseDto close(UUID contractId);

    void deleteDetail(UUID contractId, UUID detailId);
}
