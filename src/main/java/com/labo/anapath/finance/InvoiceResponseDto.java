package com.labo.anapath.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InvoiceResponseDto(
        UUID id,
        String code,
        UUID testOrderId,
        String testOrderCode,
        UUID patientId,
        String patientName,
        UUID contratId,
        String contratName,
        BigDecimal total,
        Boolean paid,
        InvoiceStatus status,
        int statusInvoice,
        String payment,
        String codeMecef,
        LocalDate dueDate,
        UUID branchId,
        LocalDateTime createdAt,
        List<InvoiceDetailDto> details
) {}
