package com.labo.anapath.inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleResponseDto(
        UUID id,
        String name,
        String code,
        BigDecimal quantity,
        BigDecimal minimumStock,
        String unit,
        BigDecimal purchasePrice,
        UUID supplierId,
        String supplierName,
        UUID branchId,
        LocalDateTime createdAt,
        String description,
        String lotNumber,
        LocalDate expirationDate
) {}
