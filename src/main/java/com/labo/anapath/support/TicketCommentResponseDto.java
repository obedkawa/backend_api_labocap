package com.labo.anapath.support;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketCommentResponseDto(
        UUID id,
        UUID ticketId,
        String content,
        UUID userId,
        String userName,
        LocalDateTime createdAt
) {}
