package com.labo.anapath.finance;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundRequestLogDto(UUID id, UUID userId, String operation, LocalDateTime createdAt) {}
