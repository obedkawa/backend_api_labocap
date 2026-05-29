package com.labo.anapath.appointment;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.security.UserPrincipal;
import com.labo.anapath.consultation.ConsultationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/calendar")
    @PreAuthorize("hasAuthority('view-appointments')")
    public ResponseEntity<ApiResponse<List<AppointmentCalendarDto>>> getCalendar(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                appointmentService.getCalendar(principal.getBranchId())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-appointments')")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(appointmentService.findById(id, principal.getBranchId())));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create-appointments')")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> create(
            @Valid @RequestBody AppointmentRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rendez-vous créé",
                        appointmentService.create(dto, principal.getBranchId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-appointments')")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AppointmentRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous mis à jour",
                appointmentService.update(id, dto, principal.getBranchId())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete-appointments')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        appointmentService.delete(id, principal.getBranchId());
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous supprimé", null));
    }

    @PostMapping("/{id}/consultation")
    @PreAuthorize("hasAuthority('create-consultations')")
    public ResponseEntity<ApiResponse<ConsultationResponseDto>> createConsultation(
            @PathVariable UUID id) {
        boolean existed = appointmentService.hasConsultation(id);
        ConsultationResponseDto dto = appointmentService.createConsultationFromAppointment(id);
        HttpStatus status = existed ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status)
                .body(ApiResponse.success(existed ? "Consultation existante" : "Consultation créée", dto));
    }
}
