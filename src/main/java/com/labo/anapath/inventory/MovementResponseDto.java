package com.labo.anapath.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse représentant un mouvement de stock.
 * Les informations de l'article (id, nom) sont aplaties dans ce record.
 */
public record MovementResponseDto(
        UUID id,
        UUID articleId,
        String articleName,
        MovementType type,
        BigDecimal quantity,
        String notes,
        UUID branchId,
        LocalDateTime createdAt
) {}
