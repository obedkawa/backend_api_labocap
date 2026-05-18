package com.labo.anapath.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatResponseDto(
        UUID id,
        UUID senderId,
        String senderName,
        UUID receiverId,
        String receiverName,
        String message,
        Boolean isRead,
        UUID branchId,
        LocalDateTime createdAt
) {}
