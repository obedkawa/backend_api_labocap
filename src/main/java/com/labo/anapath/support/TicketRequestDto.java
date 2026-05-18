package com.labo.anapath.support;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création ou la mise à jour d'un ticket de support.
 */
@Getter
@Setter
public class TicketRequestDto {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;

    private TicketPriority priority = TicketPriority.MEDIUM;
}
