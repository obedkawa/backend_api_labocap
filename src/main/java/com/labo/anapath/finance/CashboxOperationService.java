package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface CashboxOperationService {

    PageResponse<CashboxOperationResponseDto> findAll(int page, int size, UUID branchId,
                                                      UUID cashboxId, String type, LocalDate date);

    CashboxOperationResponseDto create(CashboxOperationCreateDto dto, UUID branchId);
}
