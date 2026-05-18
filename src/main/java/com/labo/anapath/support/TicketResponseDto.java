package com.labo.anapath.support;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse représentant un ticket de support interne.
 * Les informations de l'utilisateur créateur (id, nom complet) sont incluses.
 */
public record TicketResponseDto(
        UUID id,
        String ticketCode,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        UUID userId,
        String userName,
        UUID branchId,
        LocalDateTime createdAt
) {}
