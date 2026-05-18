package com.labo.anapath.finance;

import java.time.LocalDateTime;
import java.util.UUID;

public record BankResponseDto(
        UUID id,
        String name,
        String accountNumber,
        String description,
        UUID branchId,
        LocalDateTime createdAt
) {}
