package com.labo.anapath.finance;

import java.util.UUID;

public record RefundReasonResponseDto(UUID id, String label, UUID branchId) {}
