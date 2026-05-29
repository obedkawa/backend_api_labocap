package com.labo.anapath.testorder;

import java.math.BigDecimal;

/**
 * DTO de tarification contractuelle d'une analyse pour un contrat donné.
 *
 * <p>Retourné par {@code GET /api/v1/test-orders/discount?contratId=...&labTestId=...}.
 * Si le test n'est pas référencé dans le contrat, seul {@code basePrice} est renseigné
 * et la remise est zéro.
 *
 * @param basePrice          prix catalogue de l'analyse (depuis {@code LabTest.price})
 * @param contractPrice      prix négocié dans le contrat (null si absent du contrat)
 * @param discount           montant de la remise fixe (0 si absent du contrat)
 * @param priceAfterDiscount prix final après application de la remise
 */
public record DiscountDto(
        BigDecimal basePrice,
        BigDecimal contractPrice,
        BigDecimal discount,
        BigDecimal priceAfterDiscount
) {}
