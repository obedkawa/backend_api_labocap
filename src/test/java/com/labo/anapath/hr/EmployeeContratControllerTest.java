package com.labo.anapath.hr;

import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeContratControllerTest {

    @Mock EmployeeContratRepository contratRepository;
    @Mock EmployeeRepository employeeRepository;

    EmployeeContratController controller;

    private final UUID EMP_ID     = UUID.randomUUID();
    private final UUID CONTRAT_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        controller = new EmployeeContratController(contratRepository, employeeRepository);
    }

    private Employee buildEmployee() {
        Employee e = new Employee();
        ReflectionTestUtils.setField(e, "id", EMP_ID);
        return e;
    }

    private EmployeeContrat buildContrat(Employee emp) {
        EmployeeContrat c = new EmployeeContrat();
        ReflectionTestUtils.setField(c, "id", CONTRAT_ID);
        c.setEmployee(emp);
        c.setStartDate(LocalDate.of(2024, 1, 1));
        c.setSalary(new BigDecimal("350000"));
        c.setType("CDI");
        return c;
    }

    private EmployeeContratController.EmployeeContratRequestDto buildDto() {
        EmployeeContratController.EmployeeContratRequestDto dto =
                new EmployeeContratController.EmployeeContratRequestDto();
        dto.setStartDate(LocalDate.of(2024, 1, 1));
        dto.setSalary(new BigDecimal("350000"));
        dto.setType("CDI");
        return dto;
    }

    @Test
    @DisplayName("create - employé valide → 201")
    void createContrat_validRequest_returns201() {
        Employee emp = buildEmployee();
        EmployeeContrat saved = buildContrat(emp);
        when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.of(emp));
        when(contratRepository.save(any())).thenReturn(saved);

        ResponseEntity<?> response = controller.create(EMP_ID, buildDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("create - employé inconnu → ResourceNotFoundException")
    void createContrat_unknownEmployee_returns404() {
        when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.create(EMP_ID, buildDto()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - données valides → 200")
    void updateContrat_validRequest_returns200() {
        Employee emp = buildEmployee();
        EmployeeContrat existing = buildContrat(emp);
        when(contratRepository.findById(CONTRAT_ID)).thenReturn(Optional.of(existing));
        when(contratRepository.save(any())).thenReturn(existing);

        EmployeeContratController.EmployeeContratRequestDto dto = buildDto();
        dto.setSalary(new BigDecimal("400000"));

        ResponseEntity<?> response = controller.update(EMP_ID, CONTRAT_ID, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("update - contrat inconnu → ResourceNotFoundException")
    void updateContrat_unknownContrat_returns404() {
        when(contratRepository.findById(CONTRAT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.update(EMP_ID, CONTRAT_ID, buildDto()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete - contrat existant → 200 et deleteById appelé")
    void deleteContrat_existingId_returns200() {
        Employee emp = buildEmployee();
        when(contratRepository.findById(CONTRAT_ID)).thenReturn(Optional.of(buildContrat(emp)));

        ResponseEntity<?> response = controller.delete(EMP_ID, CONTRAT_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(contratRepository).deleteById(CONTRAT_ID);
    }

    @Test
    @DisplayName("delete - contrat inconnu → ResourceNotFoundException")
    void deleteContrat_unknownId_returns404() {
        when(contratRepository.findById(CONTRAT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.delete(EMP_ID, CONTRAT_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
