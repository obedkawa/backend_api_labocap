package com.labo.anapath.finance;

import java.math.BigDecimal;

public record InvoiceSearchResultDto(
        BigDecimal ca,
        BigDecimal avoir,
        BigDecimal facture,
        BigDecimal encaissement
) {}
