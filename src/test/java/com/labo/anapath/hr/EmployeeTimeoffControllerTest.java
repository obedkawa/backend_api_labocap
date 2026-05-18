package com.labo.anapath.hr;

import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.user.User;
import com.labo.anapath.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeTimeoffControllerTest {

    @Mock EmployeeTimeoffRepository timeoffRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock UserRepository userRepository;

    EmployeeTimeoffController controller;

    private final UUID EMP_ID     = UUID.randomUUID();
    private final UUID TIMEOFF_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        controller = new EmployeeTimeoffController(timeoffRepository, employeeRepository, userRepository);
    }

    private Employee buildEmployee() {
        Employee e = new Employee();
        ReflectionTestUtils.setField(e, "id", EMP_ID);
        return e;
    }

    private EmployeeTimeoff buildTimeoff(Employee emp, TimeoffStatus status) {
        EmployeeTimeoff t = new EmployeeTimeoff();
        ReflectionTestUtils.setField(t, "id", TIMEOFF_ID);
        t.setEmployee(emp);
        t.setStartDate(LocalDate.of(2026, 6, 1));
        t.setEndDate(LocalDate.of(2026, 6, 14));
        t.setStatus(status);
        return t;
    }

    private EmployeeTimeoffController.EmployeeTimeoffRequestDto buildCreateDto() {
        EmployeeTimeoffController.EmployeeTimeoffRequestDto dto =
                new EmployeeTimeoffController.EmployeeTimeoffRequestDto();
        dto.setStartDate(LocalDate.of(2026, 6, 1));
        dto.setEndDate(LocalDate.of(2026, 6, 14));
        return dto;
    }

    private EmployeeTimeoffController.TimeoffStatusUpdateDto buildStatusDto(TimeoffStatus status) {
        EmployeeTimeoffController.TimeoffStatusUpdateDto dto =
                new EmployeeTimeoffController.TimeoffStatusUpdateDto();
        dto.setStatus(status);
        return dto;
    }

    @Test
    @DisplayName("create - statut initial toujours PENDING → 201")
    void createTimeoff_validRequest_returns201_withStatusPending() {
        Employee emp = buildEmployee();
        EmployeeTimeoff saved = buildTimeoff(emp, TimeoffStatus.PENDING);

        when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.of(emp));
        when(timeoffRepository.save(any())).thenReturn(saved);

        ResponseEntity<?> response = controller.create(EMP_ID, buildCreateDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(saved.getStatus()).isEqualTo(TimeoffStatus.PENDING);
    }

    @Test
    @DisplayName("create - employé inconnu → ResourceNotFoundException")
    void createTimeoff_unknownEmployee_returns404() {
        when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.create(EMP_ID, buildCreateDto()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateStatus → APPROVED : User.isActive mis à false")
    void updateStatus_toApproved_setsUserIsActiveFalse() {
        User user = new User();
        // user.isActive == true by default

        Employee emp = buildEmployee();
        emp.setUser(user);

        EmployeeTimeoff timeoff = buildTimeoff(emp, TimeoffStatus.PENDING);
        when(timeoffRepository.findById(TIMEOFF_ID)).thenReturn(Optional.of(timeoff));
        when(timeoffRepository.save(any())).thenReturn(timeoff);

        controller.updateStatus(EMP_ID, TIMEOFF_ID, buildStatusDto(TimeoffStatus.APPROVED));

        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("updateStatus → APPROVED, employé sans User : pas de mise à jour User")
    void updateStatus_toApproved_employeeWithoutUser_noUserUpdate() {
        Employee emp = buildEmployee();
        // emp.getUser() == null

        EmployeeTimeoff timeoff = buildTimeoff(emp, TimeoffStatus.PENDING);
        when(timeoffRepository.findById(TIMEOFF_ID)).thenReturn(Optional.of(timeoff));
        when(timeoffRepository.save(any())).thenReturn(timeoff);

        controller.updateStatus(EMP_ID, TIMEOFF_ID, buildStatusDto(TimeoffStatus.APPROVED));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateStatus APPROVED → REJECTED : User.isActive remis à true")
    void updateStatus_toRejected_fromApproved_setsUserIsActiveTrue() {
        User user = new User();
        user.setActive(false);

        Employee emp = buildEmployee();
        emp.setUser(user);

        EmployeeTimeoff timeoff = buildTimeoff(emp, TimeoffStatus.APPROVED);
        when(timeoffRepository.findById(TIMEOFF_ID)).thenReturn(Optional.of(timeoff));
        when(timeoffRepository.save(any())).thenReturn(timeoff);

        controller.updateStatus(EMP_ID, TIMEOFF_ID, buildStatusDto(TimeoffStatus.REJECTED));

        assertThat(user.isActive()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("updateStatus PENDING → REJECTED : User déjà actif, aucune modification")
    void updateStatus_toRejected_userAlreadyActive_noChange() {
        User user = new User();
        // user.isActive == true, and previousStatus == PENDING (not APPROVED)

        Employee emp = buildEmployee();
        emp.setUser(user);

        EmployeeTimeoff timeoff = buildTimeoff(emp, TimeoffStatus.PENDING);
        when(timeoffRepository.findById(TIMEOFF_ID)).thenReturn(Optional.of(timeoff));
        when(timeoffRepository.save(any())).thenReturn(timeoff);

        controller.updateStatus(EMP_ID, TIMEOFF_ID, buildStatusDto(TimeoffStatus.REJECTED));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete - statut PENDING → 200 + deleteById appelé")
    void deleteTimeoff_pendingStatus_returns200() {
        Employee emp = buildEmployee();
        EmployeeTimeoff timeoff = buildTimeoff(emp, TimeoffStatus.PENDING);
        when(timeoffRepository.findById(TIMEOFF_ID)).thenReturn(Optional.of(timeoff));

        ResponseEntity<?> response = controller.delete(EMP_ID, TIMEOFF_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(timeoffRepository).deleteById(TIMEOFF_ID);
    }

    @Test
    @DisplayName("delete - statut APPROVED → InvalidOperationException")
    void deleteTimeoff_approvedStatus_throws() {
        Employee emp = buildEmployee();
        EmployeeTimeoff timeoff = buildTimeoff(emp, TimeoffStatus.APPROVED);
        when(timeoffRepository.findById(TIMEOFF_ID)).thenReturn(Optional.of(timeoff));

        assertThatThrownBy(() -> controller.delete(EMP_ID, TIMEOFF_ID))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    @DisplayName("delete - congé inconnu → ResourceNotFoundException")
    void deleteTimeoff_unknownId_returns404() {
        when(timeoffRepository.findById(TIMEOFF_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.delete(EMP_ID, TIMEOFF_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
