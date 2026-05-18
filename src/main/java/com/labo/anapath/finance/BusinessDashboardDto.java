package com.labo.anapath.finance;

import java.math.BigDecimal;

public record BusinessDashboardDto(
        BigDecimal totalToday,
        BigDecimal totalMonth,
        BigDecimal totalLastMonth
) {}
