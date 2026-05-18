package com.labo.anapath.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReportResponseDto(
        UUID id,
        UUID testOrderId,
        String testOrderCode,
        UUID titleId,
        String titleName,
        String content,
        String contentMicro,
        String comment,
        String commentSup,
        String descriptionSupplementaire,
        String descriptionSupplementaireMicro,
        ReportStatus status,
        boolean isDelivered,
        boolean isCalled,
        String receiverName,
        LocalDateTime signatureDate,
        LocalDateTime deliveryDate,
        LocalDateTime callDate,
        UUID signatory1Id,
        String signatory1Name,
        UUID branchId,
        List<String> tagNames,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
