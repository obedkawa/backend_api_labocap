package com.labo.anapath.finance;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceDetailDto(
        UUID id,
        UUID labTestId,
        String testName,
        Double price,
        Double discount,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal total
) {}
