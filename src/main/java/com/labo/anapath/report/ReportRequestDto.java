package com.labo.anapath.report;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ReportRequestDto {

    /** Si fourni → mise à jour du rapport existant ; sinon → création. */
    private UUID reportId;

    /** Obligatoire à la création pour lier le CR à son bon d'examen. */
    private UUID testOrderId;

    private UUID titleId;
    private String content;
    private String contentMicro;
    private String comment;
    private String commentSup;
    private String descriptionSupplementaire;
    private String descriptionSupplementaireMicro;
    private UUID reviewedById;
    private UUID signatory1Id;
    private UUID signatory2Id;
    private UUID signatory3Id;
    private String receiverName;

    /** "VALIDATED" pour valider ; "DRAFT" (ou null) pour brouillon. */
    private String status;

    private List<UUID> tagIds = new ArrayList<>();
}
