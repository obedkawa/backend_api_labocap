package com.labo.anapath.appointment;

import com.labo.anapath.consultation.ConsultationResponseDto;

import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    List<AppointmentCalendarDto> getCalendar(UUID branchId);

    AppointmentResponseDto findById(UUID id, UUID branchId);

    AppointmentResponseDto create(AppointmentRequestDto dto, UUID branchId);

    AppointmentResponseDto update(UUID id, AppointmentRequestDto dto, UUID branchId);

    void delete(UUID id, UUID branchId);

    ConsultationResponseDto createConsultationFromAppointment(UUID appointmentId);

    boolean hasConsultation(UUID appointmentId);
}
