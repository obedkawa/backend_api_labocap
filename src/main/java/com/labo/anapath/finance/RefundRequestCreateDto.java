package com.labo.anapath.finance;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class RefundRequestCreateDto {

    @NotNull(message = "La facture est obligatoire")
    private UUID invoiceId;

    @NotNull(message = "Le motif est obligatoire")
    private UUID refundReasonId;

    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal montant;

    private String note;
    private String attachment;
}
