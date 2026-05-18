package com.labo.anapath.report;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppelResponseDto(UUID id, UUID reportId, String appelId, LocalDateTime createdAt) {}
