package com.labo.anapath.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BankDepositResponseDto(
        UUID id,
        UUID bankId,
        String bankName,
        UUID cashboxId,
        BigDecimal amount,
        LocalDate date,
        String description,
        String attachement,
        UUID branchId,
        LocalDateTime createdAt
) {}
