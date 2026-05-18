package com.labo.anapath.contract;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ContratRequestDto {

    private String name;

    private String type;

    private String description;

    private UUID hospitalId;

    private UUID clientId;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    private LocalDate endDate;

    private int nbrTests;

    private String status = "INACTIF";

    private Boolean invoiceUnique = true;

    private List<ContratDetailRequestDto> details = new ArrayList<>();

    @Getter
    @Setter
    public static class ContratDetailRequestDto {
        private UUID labTestId;
        private BigDecimal price;
    }
}
