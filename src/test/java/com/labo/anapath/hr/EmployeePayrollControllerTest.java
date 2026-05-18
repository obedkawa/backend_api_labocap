package com.labo.anapath.hr;

import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeePayrollControllerTest {

    @Mock EmployeePayrollRepository payrollRepository;
    @Mock EmployeeRepository employeeRepository;

    EmployeePayrollController controller;

    private final UUID EMP_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        controller = new EmployeePayrollController(payrollRepository, employeeRepository);
    }

    private Employee buildEmployee() {
        Employee e = new Employee();
        ReflectionTestUtils.setField(e, "id", EMP_ID);
        return e;
    }

    private EmployeePayroll buildPayroll(Employee emp, BigDecimal gross, BigDecimal deductions) {
        EmployeePayroll p = new EmployeePayroll();
        ReflectionTestUtils.setField(p, "id", UUID.randomUUID());
        p.setEmployee(emp);
        p.setMonth(5);
        p.setYear(2026);
        p.setGrossSalary(gross);
        p.setDeductions(deductions);
        p.setNetSalary(gross.subtract(deductions));
        return p;
    }

    @Test
    @DisplayName("create - salaire net calculé automatiquement")
    void createPayroll_validRequest_returns201_withNetSalaryCalculated() {
        Employee emp = buildEmployee();
        BigDecimal gross = new BigDecimal("350000");
        BigDecimal deductions = new BigDecimal("25000");
        EmployeePayroll saved = buildPayroll(emp, gross, deductions);

        when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.of(emp));
        when(payrollRepository.save(any())).thenReturn(saved);

        EmployeePayrollController.EmployeePayrollRequestDto dto =
                new EmployeePayrollController.EmployeePayrollRequestDto();
        dto.setMonth(5);
        dto.setYear(2026);
        dto.setGrossSalary(gross);
        dto.setDeductions(deductions);

        ResponseEntity<?> response = controller.create(EMP_ID, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(saved.getNetSalary()).isEqualByComparingTo(new BigDecimal("325000"));
    }

    @Test
    @DisplayName("create - déductions null → traité comme ZERO, net = brut")
    void createPayroll_withNullDeductions_usesZero() {
        Employee emp = buildEmployee();
        BigDecimal gross = new BigDecimal("300000");
        EmployeePayroll saved = buildPayroll(emp, gross, BigDecimal.ZERO);

        when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.of(emp));
        when(payrollRepository.save(any())).thenReturn(saved);

        EmployeePayrollController.EmployeePayrollRequestDto dto =
                new EmployeePayrollController.EmployeePayrollRequestDto();
        dto.setMonth(4);
        dto.setYear(2026);
        dto.setGrossSalary(gross);
        dto.setDeductions(null);

        controller.create(EMP_ID, dto);

        ArgumentCaptor<EmployeePayroll> captor = ArgumentCaptor.forClass(EmployeePayroll.class);
        verify(payrollRepository).save(captor.capture());
        assertThat(captor.getValue().getDeductions()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(captor.getValue().getNetSalary()).isEqualByComparingTo(gross);
    }

    @Test
    @DisplayName("create - employé inconnu → ResourceNotFoundException")
    void createPayroll_unknownEmployee_returns404() {
        when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.empty());

        EmployeePayrollController.EmployeePayrollRequestDto dto =
                new EmployeePayrollController.EmployeePayrollRequestDto();
        dto.setMonth(5);
        dto.setYear(2026);
        dto.setGrossSalary(new BigDecimal("300000"));

        assertThatThrownBy(() -> controller.create(EMP_ID, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findAll - retourne page paginée")
    void findAllPayrolls_returnsPaginatedList() {
        Employee emp = buildEmployee();
        EmployeePayroll payroll = buildPayroll(emp, new BigDecimal("300000"), BigDecimal.ZERO);
        Page<EmployeePayroll> page = new PageImpl<>(List.of(payroll));
        when(payrollRepository.findByEmployeeId(eq(EMP_ID), any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = controller.findAll(EMP_ID, 0, 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
