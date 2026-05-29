package com.labo.anapath.consultation;

import java.time.LocalDateTime;
import java.util.UUID;

public record TypeConsultationResponseDto(UUID id, String name, UUID branchId, LocalDateTime createdAt) {}
