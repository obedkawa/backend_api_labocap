package com.labo.anapath.doc;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocVersionResponseDto(
        UUID id,
        UUID docId,
        Integer version,
        String title,
        String attachment,
        Long fileSize,
        UUID userId,
        LocalDateTime createdAt
) {}
