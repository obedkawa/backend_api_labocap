package com.labo.anapath.consultation;

import com.labo.anapath.appointment.Appointment;
import com.labo.anapath.common.audit.AuditableEntity;
import com.labo.anapath.doctor.Doctor;
import com.labo.anapath.patient.Patient;
import com.labo.anapath.prestation.Prestation;
import com.labo.anapath.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
@SQLDelete(sql = "UPDATE consultations SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Consultation extends AuditableEntity {

    @Column(name = "code", length = 50)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_consultation_id")
    private TypeConsultation typeConsultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestation_id")
    private Prestation prestation;

    // Champ nommé attribuate_doctor_id en BDD (typo Laravel préservée)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribuate_doctor_id")
    private User attribuateDoctor;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "fees", precision = 10, scale = 2)
    private BigDecimal fees;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "motif", columnDefinition = "TEXT")
    private String motif;

    @Column(name = "anamnese", columnDefinition = "TEXT")
    private String anamnese;

    @Column(name = "antecedent", columnDefinition = "TEXT")
    private String antecedent;

    @Column(name = "examen_physique", columnDefinition = "TEXT")
    private String examenPhysique;

    @Column(name = "diagnostic", columnDefinition = "TEXT")
    private String diagnostic;

    @Column(name = "payment_mode", length = 30)
    private String paymentMode;

    @Column(name = "next_appointment")
    private LocalDateTime nextAppointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
}
