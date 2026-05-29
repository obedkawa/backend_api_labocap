package com.labo.anapath.hr;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface EmployeeContratService {
    PageResponse<EmployeeContratResponseDto> findAll(int page, int size, UUID employeeId);
    EmployeeContratResponseDto create(EmployeeContratRequestDto dto, UUID employeeId);
    EmployeeContratResponseDto update(UUID id, EmployeeContratRequestDto dto);
    void delete(UUID id);
}
