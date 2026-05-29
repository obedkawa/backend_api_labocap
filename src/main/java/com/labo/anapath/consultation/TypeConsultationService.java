package com.labo.anapath.consultation;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface TypeConsultationService {
    PageResponse<TypeConsultationResponseDto> findAll(int page, int size, UUID branchId);
    TypeConsultationResponseDto findById(UUID id);
    TypeConsultationResponseDto create(TypeConsultationRequestDto dto, UUID branchId);
    TypeConsultationResponseDto update(UUID id, TypeConsultationRequestDto dto);
    void delete(UUID id);
}
