package com.labo.anapath.report;

import java.util.UUID;

public record CallResponseDto(String appelId, UUID reportId, String audioUrl) {}
