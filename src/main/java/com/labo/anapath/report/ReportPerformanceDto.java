package com.labo.anapath.report;

public record ReportPerformanceDto(
        long totalReports,
        long withinDeadline,
        long beyondDeadline,
        double percentageInDeadline,
        double percentageOverDeadline
) {}
