package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface CashboxVoucherService {

    PageResponse<CashboxVoucherResponseDto> findAll(int page, int size, UUID branchId);

    CashboxVoucherResponseDto findById(UUID id);

    CashboxVoucherResponseDto create(CashboxVoucherRequestDto dto, UUID branchId);

    CashboxVoucherResponseDto update(UUID id, CashboxVoucherRequestDto dto);

    void delete(UUID id);

    CashboxVoucherResponseDto addDetail(UUID voucherId, CashboxVoucherDetailRequestDto dto, UUID branchId);

    void removeDetail(UUID voucherId, UUID detailId);

    CashboxVoucherResponseDto updateStatus(UUID voucherId, CashboxVoucherStatusDto dto, UUID branchId, UUID userId);
}
