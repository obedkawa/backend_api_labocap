package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface InvoiceService {

    PageResponse<InvoiceResponseDto> findAll(int page, int size, UUID branchId);

    InvoiceResponseDto findById(UUID id);

    InvoiceResponseDto create(InvoiceRequestDto dto, UUID branchId);

    InvoiceResponseDto update(UUID id, InvoiceRequestDto dto);

    void delete(UUID id);

    InvoiceResponseDto markAsPaid(UUID invoiceId, InvoiceStatusUpdateDto dto);

    BusinessDashboardDto getBusinessDashboard(UUID branchId);

    InvoiceSearchResultDto searchByPeriod(LocalDate startDate, LocalDate endDate, UUID branchId);
}
