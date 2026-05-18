package com.labo.anapath.support;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface ProblemReportService {
    PageResponse<ProblemReportResponseDto> findAll(int page, int size, UUID branchId);
    ProblemReportResponseDto findById(UUID id);
    ProblemReportResponseDto create(ProblemReportRequestDto dto, UUID branchId);
    ProblemReportResponseDto updateStatus(UUID id, String status);
}
