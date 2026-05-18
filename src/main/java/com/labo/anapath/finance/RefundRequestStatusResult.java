package com.labo.anapath.finance;

import java.util.UUID;

public record RefundRequestStatusResult(UUID invoiceId, String status) {}
