package com.labo.anapath.report;

import java.util.List;

public record ReportSuiviDto(
        ExamenStats examens,
        RapportStats rapports,
        MacroStats macros,
        PatientCalledStats patientCalled,
        List<Integer> listYears
) {
    public record ExamenStats(
            long histologie,
            long immunoExterne,
            long immunoInterne,
            long cytologie,
            long totalGeneral
    ) {}

    public record RapportStats(
            long attente,
            long termine,
            long affecte
    ) {}

    public record MacroStats(long pathology) {}

    public record PatientCalledStats(
            long called,
            long notCalled,
            long deliver,
            long notDeliver
    ) {}
}
