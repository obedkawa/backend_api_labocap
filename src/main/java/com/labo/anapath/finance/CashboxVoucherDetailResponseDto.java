package com.labo.anapath.finance;

import java.math.BigDecimal;
import java.util.UUID;

public record CashboxVoucherDetailResponseDto(
        UUID id,
        String itemName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal lineAmount
) {}
