package com.labo.anapath.finance;

import java.math.BigDecimal;

public record CashboxDailySummaryDto(
        BigDecimal totalEspeces,
        BigDecimal totalMobileMoney,
        BigDecimal totalCheques,
        BigDecimal totalVirement,
        BigDecimal total
) {}
