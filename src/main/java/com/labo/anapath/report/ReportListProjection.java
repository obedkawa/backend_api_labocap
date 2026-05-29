package com.labo.anapath.report;

import java.time.LocalDateTime;

public interface ReportListProjection {
    String getId();
    String getReportCode();
    String getTestOrderId();
    String getTestOrderCode();
    String getPatientId();
    String getPatientCode();
    String getPatientFirstname();
    String getPatientLastname();
    String getPatientPhone();
    String getTypeOrderTitle();
    String getStatus();
    Boolean getIsDelivered();
    Boolean getIsCalled();
    LocalDateTime getSignatureDate();
    LocalDateTime getCreatedAt();
}
