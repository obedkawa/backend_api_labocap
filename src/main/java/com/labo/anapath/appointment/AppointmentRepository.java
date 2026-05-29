package com.labo.anapath.appointment;

import com.labo.anapath.dashboard.DashboardDto;
import com.labo.anapath.dashboard.DashboardProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByBranchId(UUID branchId);

    Page<Appointment> findByBranchId(UUID branchId, Pageable pageable);

    // Dashboard — rendez-vous en attente pour un médecin interne
    @Query(value = """
            SELECT a.id::text as id,
                   CONCAT(p.lastname, ' ', p.firstname) as patientName,
                   a.date::text as date,
                   a.priority as priority,
                   a.status as status,
                   a.message as message
            FROM appointments a JOIN patients p ON a.patient_id = p.id
            WHERE a.user_id = :userId AND a.status = 'pending'
              AND a.deleted_at IS NULL
            ORDER BY a.date ASC
            """, nativeQuery = true)
    List<DashboardProjection.AppointmentItem> findPendingByDoctorInterne(@Param("userId") UUID userId);
}
