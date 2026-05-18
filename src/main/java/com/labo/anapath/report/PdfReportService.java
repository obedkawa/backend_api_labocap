package com.labo.anapath.report;

import java.util.UUID;

public interface PdfReportService {

    byte[] generatePdf(UUID reportId, UUID userId);
}
