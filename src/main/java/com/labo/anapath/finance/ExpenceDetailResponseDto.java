package com.labo.anapath.finance;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenceDetailResponseDto(
        UUID id,
        String articleName,
        UUID articleId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal lineAmount
) {}
