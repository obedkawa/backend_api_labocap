package com.labo.anapath.dashboard;

/**
 * Interfaces de projection Spring Data JPA pour les requêtes natives du dashboard.
 * Les records Java ne sont pas supportés par les projections natives — on utilise
 * des interfaces avec getters que Spring peut instancier par proxy.
 */
public class DashboardProjection {

    public interface TopExamen {
        String getTestName();
        Long getTotalDemandes();
    }

    public interface DoctorStat {
        String getId();
        String getDoctor();
        Long getAssigne();
        Long getTraite();
    }

    public interface ByItem {
        String getNom();
        Long getTotalPatients();
    }

    public interface ConnectedUser {
        String getId();
        String getLastname();
        String getFirstname();
        String getEmail();
    }

    public interface DayRevenue {
        String getDate();
        java.math.BigDecimal getTotal();
    }

    public interface ReportToday {
        String getId();
        String getTestOrderId();
        String getCode();
        String getPatientLastname();
        String getPatientFirstname();
        String getCreatedAt();
        Integer getStatus();
        Boolean getIsDeliver();
        String getInvoiceId();
    }

    public interface AppointmentItem {
        String getId();
        String getPatientName();
        String getDate();
        String getPriority();
        String getStatus();
        String getMessage();
    }

    public interface DoctorOrder {
        String getId();
        String getCode();
        String getCreatedAt();
        String getPatientFirstname();
        String getPatientLastname();
        Integer getReportStatus();
    }
}
