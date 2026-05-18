package com.labo.anapath.inventory;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupplierCategoryResponseDto(
        UUID id,
        String name,
        String description,
        UUID branchId,
        LocalDateTime createdAt
) {}
