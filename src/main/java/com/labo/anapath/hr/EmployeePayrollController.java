package com.labo.anapath.hr;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contrôleur REST gérant les fiches de paie des employés.
 * <p>
 * Toutes les routes sont imbriquées sous {@code /api/v1/employees/{employeeId}/payrolls}.
 * Seule la consultation et la création sont supportées ; la modification
 * de fiches de paie existantes n'est pas exposée.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/employees/{employeeId}/payrolls")
@RequiredArgsConstructor
public class EmployeePayrollController {

    private final EmployeePayrollRepository employeePayrollRepository;
    private final EmployeeRepository employeeRepository;

    /** DTO de réponse interne représentant une fiche de paie. */
    record EmployeePayrollResponseDto(
            UUID id,
            UUID employeeId,
            int month,
            int year,
            BigDecimal grossSalary,
            BigDecimal deductions,
            BigDecimal netSalary,
            LocalDate paidAt,
            LocalDateTime createdAt
    ) {}

    /** DTO de requête interne pour la création d'une fiche de paie. */
    @Getter
    @Setter
    static class EmployeePayrollRequestDto {
        @NotNull(message = "Le mois est obligatoire")
        private Integer month;

        @NotNull(message = "L'année est obligatoire")
        private Integer year;

        @NotNull(message = "Le salaire brut est obligatoire")
        private BigDecimal grossSalary;

        private BigDecimal deductions = BigDecimal.ZERO;

        private LocalDate paidAt;
    }

    /**
     * Retourne la liste paginée des fiches de paie d'un employé.
     *
     * @param employeeId identifiant UUID de l'employé
     * @param page       numéro de page
     * @param size       taille de la page
     * @return page de fiches de paie
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-hr')")
    public ResponseEntity<ApiResponse<PageResponse<EmployeePayrollResponseDto>>> findAll(
            @PathVariable UUID employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                employeePayrollRepository.findByEmployeeId(employeeId,
                        PageRequest.of(page, size)).map(p -> new EmployeePayrollResponseDto(
                        p.getId(), p.getEmployee().getId(), p.getMonth(), p.getYear(),
                        p.getGrossSalary(), p.getDeductions(), p.getNetSalary(),
                        p.getPaidAt(), p.getCreatedAt())))));
    }

    /**
     * Crée une fiche de paie pour un employé.
     * Le salaire net est calculé automatiquement comme : salaire brut − déductions.
     *
     * @param employeeId identifiant UUID de l'employé
     * @param dto        données de la fiche de paie
     * @return la fiche de paie créée avec le statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('manage-employees')")
    @Transactional
    public ResponseEntity<ApiResponse<EmployeePayrollResponseDto>> create(
            @PathVariable UUID employeeId,
            @Valid @RequestBody EmployeePayrollRequestDto dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", employeeId));
        EmployeePayroll payroll = new EmployeePayroll();
        payroll.setEmployee(employee);
        payroll.setMonth(dto.getMonth());
        payroll.setYear(dto.getYear());
        payroll.setGrossSalary(dto.getGrossSalary());
        BigDecimal deductions = dto.getDeductions() != null ? dto.getDeductions() : BigDecimal.ZERO;
        payroll.setDeductions(deductions);
        // Calcul automatique du net : brut - déductions
        payroll.setNetSalary(dto.getGrossSalary().subtract(deductions));
        payroll.setPaidAt(dto.getPaidAt());
        EmployeePayroll saved = employeePayrollRepository.save(payroll);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fiche de paie créée", new EmployeePayrollResponseDto(
                        saved.getId(), saved.getEmployee().getId(), saved.getMonth(), saved.getYear(),
                        saved.getGrossSalary(), saved.getDeductions(), saved.getNetSalary(),
                        saved.getPaidAt(), saved.getCreatedAt())));
    }
}
