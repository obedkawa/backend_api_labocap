package com.labo.anapath.contract;

import java.math.BigDecimal;
import java.util.UUID;

public record DetailsContratDto(
        UUID id,
        UUID labTestId,
        String labTestName,
        BigDecimal price,
        BigDecimal pourcentage,
        BigDecimal amountRemise,
        BigDecimal amountAfterRemise,
        UUID categoryTestId
) {}
