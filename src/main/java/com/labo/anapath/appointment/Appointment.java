package com.labo.anapath.appointment;

import com.labo.anapath.common.audit.AuditableEntity;
import com.labo.anapath.consultation.Consultation;
import com.labo.anapath.patient.Patient;
import com.labo.anapath.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@SQLDelete(sql = "UPDATE appointments SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Appointment extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Médecin interne (FK vers users.id, PAS doctors)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User doctorInterne;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "priority", length = 20)
    private String priority;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY)
    private Consultation consultation;
}
