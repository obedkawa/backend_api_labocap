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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contrôleur REST gérant les contrats de travail des employés.
 * <p>
 * Toutes les routes sont imbriquées sous {@code /api/v1/employees/{employeeId}/contrats}.
 * Le contrôleur accède directement au repository car il n'y a pas de couche
 * service dédiée pour les contrats.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/employees/{employeeId}/contrats")
@RequiredArgsConstructor
public class EmployeeContratController {

    private final EmployeeContratRepository employeeContratRepository;
    private final EmployeeRepository employeeRepository;

    /** DTO de réponse interne représentant un contrat de travail. */
    record EmployeeContratResponseDto(
            UUID id,
            UUID employeeId,
            String type,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal salary,
            LocalDateTime createdAt
    ) {}

    /** DTO de requête interne pour la création ou la mise à jour d'un contrat. */
    @Getter
    @Setter
    static class EmployeeContratRequestDto {
        @NotNull(message = "La date de début est obligatoire")
        private LocalDate startDate;

        private LocalDate endDate;

        private String type;

        @NotNull(message = "Le salaire est obligatoire")
        private BigDecimal salary;
    }

    /**
     * Retourne la liste paginée des contrats d'un employé.
     *
     * @param employeeId identifiant UUID de l'employé
     * @param page       numéro de page
     * @param size       taille de la page
     * @return page de contrats
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-hr')")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeContratResponseDto>>> findAll(
            @PathVariable UUID employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                employeeContratRepository.findByEmployeeId(employeeId,
                        PageRequest.of(page, size)).map(c -> new EmployeeContratResponseDto(
                        c.getId(), c.getEmployee().getId(), c.getType(),
                        c.getStartDate(), c.getEndDate(), c.getSalary(), c.getCreatedAt())))));
    }

    /**
     * Crée un nouveau contrat de travail pour un employé.
     *
     * @param employeeId identifiant UUID de l'employé concerné
     * @param dto        données du contrat à créer
     * @return le contrat créé avec le statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('manage-employees')")
    @Transactional
    public ResponseEntity<ApiResponse<EmployeeContratResponseDto>> create(
            @PathVariable UUID employeeId,
            @Valid @RequestBody EmployeeContratRequestDto dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", employeeId));
        EmployeeContrat contrat = new EmployeeContrat();
        contrat.setEmployee(employee);
        contrat.setType(dto.getType());
        contrat.setStartDate(dto.getStartDate());
        contrat.setEndDate(dto.getEndDate());
        contrat.setSalary(dto.getSalary());
        EmployeeContrat saved = employeeContratRepository.save(contrat);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contrat créé", new EmployeeContratResponseDto(
                        saved.getId(), saved.getEmployee().getId(), saved.getType(),
                        saved.getStartDate(), saved.getEndDate(), saved.getSalary(), saved.getCreatedAt())));
    }

    /**
     * Met à jour un contrat de travail existant.
     *
     * @param employeeId identifiant UUID de l'employé (utilisé pour le routage)
     * @param id         identifiant UUID du contrat à modifier
     * @param dto        nouvelles données du contrat
     * @return le contrat mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-employees')")
    @Transactional
    public ResponseEntity<ApiResponse<EmployeeContratResponseDto>> update(
            @PathVariable UUID employeeId,
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeContratRequestDto dto) {
        EmployeeContrat contrat = employeeContratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat employé", id));
        contrat.setType(dto.getType());
        contrat.setStartDate(dto.getStartDate());
        contrat.setEndDate(dto.getEndDate());
        contrat.setSalary(dto.getSalary());
        EmployeeContrat saved = employeeContratRepository.save(contrat);
        return ResponseEntity.ok(ApiResponse.success("Contrat mis à jour", new EmployeeContratResponseDto(
                saved.getId(), saved.getEmployee().getId(), saved.getType(),
                saved.getStartDate(), saved.getEndDate(), saved.getSalary(), saved.getCreatedAt())));
    }

    /**
     * Supprime un contrat de travail par son identifiant.
     *
     * @param employeeId identifiant UUID de l'employé (utilisé pour le routage)
     * @param id         identifiant UUID du contrat à supprimer
     * @return réponse vide confirmant la suppression
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-employees')")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID employeeId, @PathVariable UUID id) {
        employeeContratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat employé", id));
        employeeContratRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Contrat supprimé", null));
    }
}
