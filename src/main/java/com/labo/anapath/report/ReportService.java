package com.labo.anapath.report;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface ReportService {

    PageResponse<ReportResponseDto> findAll(int page, int size, UUID branchId);

    PageResponse<ReportResponseDto> findAll(int page, int size, UUID branchId, Integer month, Integer year, UUID doctorId);

    ReportResponseDto findById(UUID id);

    ReportDetailDto findDetailById(UUID id);

    ReportResponseDto create(ReportRequestDto dto, UUID branchId);

    ReportResponseDto createOrUpdate(ReportRequestDto dto, UUID branchId);

    ReportResponseDto update(UUID id, ReportRequestDto dto);

    void delete(UUID id);

    ReportResponseDto validate(UUID id, UUID userId);

    ReportResponseDto deliver(UUID id, String receiverName, UUID userId);

    ReportResponseDto markDelivered(UUID id, UUID userId);

    ReportResponseDto markInformed(UUID id, UUID userId);

    ReportResponseDto storeSignature(UUID id, StoreSignatureRequestDto dto, UUID userId);

    ReportSuiviDto getSuivi(UUID branchId, Integer month, Integer year);

    com.labo.anapath.setting.SettingReportTemplate getTemplate(UUID reportId);

    ReportResponseDto setTemplate(UUID reportId, UUID templateId);

    void logAction(UUID reportId, String action, UUID userId);
}
