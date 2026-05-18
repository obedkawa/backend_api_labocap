package com.labo.anapath.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RefundRequestResponseDto(
        UUID id,
        UUID invoiceId,
        UUID refundReasonId,
        BigDecimal montant,
        String note,
        String attachment,
        String code,
        String status,
        List<RefundRequestLogDto> logs,
        UUID branchId,
        LocalDateTime createdAt
) {}
