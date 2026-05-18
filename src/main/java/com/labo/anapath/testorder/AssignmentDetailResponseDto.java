package com.labo.anapath.testorder;

import java.util.UUID;

public record AssignmentDetailResponseDto(UUID id, UUID testOrderId, String testOrderCode, String note) {}
