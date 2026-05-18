package com.labo.anapath.inventory;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupplierResponseDto(
        UUID id,
        String name,
        String phone,
        String email,
        String address,
        String category,
        UUID categoryId,
        String categoryName,
        UUID branchId,
        LocalDateTime createdAt
) {}
