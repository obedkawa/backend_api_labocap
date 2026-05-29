package com.labo.anapath.hr;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeTimeoffControllerTest {

    @Mock EmployeeTimeoffService employeeTimeoffService;

    EmployeeTimeoffController controller;

    private final UUID EMP_ID     = UUID.randomUUID();
    private final UUID TIMEOFF_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        controller = new EmployeeTimeoffController(employeeTimeoffService);
    }

    private EmployeeTimeoffResponseDto dummyDto(TimeoffStatus status) {
        return new EmployeeTimeoffResponseDto(TIMEOFF_ID, EMP_ID,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 14), null, status, null);
    }

    private EmployeeTimeoffRequestDto buildCreateDto() {
        EmployeeTimeoffRequestDto dto = new EmployeeTimeoffRequestDto();
        dto.setStartDate(LocalDate.of(2026, 6, 1));
        dto.setEndDate(LocalDate.of(2026, 6, 14));
        return dto;
    }

    private TimeoffStatusUpdateDto buildStatusDto(TimeoffStatus status) {
        TimeoffStatusUpdateDto dto = new TimeoffStatusUpdateDto();
        dto.setStatus(status);
        return dto;
    }

    @Test
    @DisplayName("create - retourne 201")
    void createTimeoff_validRequest_returns201() {
        when(employeeTimeoffService.create(any(), eq(EMP_ID))).thenReturn(dummyDto(TimeoffStatus.PENDING));

        ResponseEntity<?> response = controller.create(EMP_ID, buildCreateDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("create - employé inconnu → ResourceNotFoundException propagée")
    void createTimeoff_unknownEmployee_throws() {
        when(employeeTimeoffService.create(any(), eq(EMP_ID)))
                .thenThrow(new ResourceNotFoundException("Employé", EMP_ID));

        assertThatThrownBy(() -> controller.create(EMP_ID, buildCreateDto()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateStatus → 200")
    void updateStatus_returns200() {
        when(employeeTimeoffService.updateStatus(eq(TIMEOFF_ID), any()))
                .thenReturn(dummyDto(TimeoffStatus.APPROVED));

        ResponseEntity<?> response = controller.updateStatus(EMP_ID, TIMEOFF_ID, buildStatusDto(TimeoffStatus.APPROVED));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("delete - retourne 200 et service.delete appelé")
    void deleteTimeoff_returns200() {
        ResponseEntity<?> response = controller.delete(EMP_ID, TIMEOFF_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(employeeTimeoffService).delete(TIMEOFF_ID);
    }

    @Test
    @DisplayName("delete - statut APPROVED → InvalidOperationException propagée")
    void deleteTimeoff_approvedStatus_throws() {
        doThrow(new InvalidOperationException("Impossible de supprimer un congé approuvé"))
                .when(employeeTimeoffService).delete(TIMEOFF_ID);

        assertThatThrownBy(() -> controller.delete(EMP_ID, TIMEOFF_ID))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    @DisplayName("delete - congé inconnu → ResourceNotFoundException propagée")
    void deleteTimeoff_unknownId_throws() {
        doThrow(new ResourceNotFoundException("Congé", TIMEOFF_ID))
                .when(employeeTimeoffService).delete(TIMEOFF_ID);

        assertThatThrownBy(() -> controller.delete(EMP_ID, TIMEOFF_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
