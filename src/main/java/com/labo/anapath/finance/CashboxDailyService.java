package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

public interface CashboxDailyService {

    CashboxDailyResponseDto openOrUpdate(CashboxDailyOpenDto dto, UUID branchId, UUID userId);

    PageResponse<CashboxDailyResponseDto> findAll(int page, int size, UUID branchId);

    CashboxDailyResponseDto findById(UUID id);

    CashboxDailyResponseDto closeCashbox(UUID id, CashboxDailyCloseDto dto, UUID userId);

    CashboxDailySummaryDto getDailySummary(UUID branchId);

    void delete(UUID id);
}
