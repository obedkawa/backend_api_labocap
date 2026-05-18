package com.labo.anapath.hr;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.user.User;
import com.labo.anapath.user.UserRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees/{employeeId}/timeoffs")
@RequiredArgsConstructor
public class EmployeeTimeoffController {

    private final EmployeeTimeoffRepository employeeTimeoffRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    record EmployeeTimeoffResponseDto(
            UUID id,
            UUID employeeId,
            LocalDate startDate,
            LocalDate endDate,
            String reason,
            TimeoffStatus status,
            LocalDateTime createdAt
    ) {}

    @Getter
    @Setter
    static class EmployeeTimeoffRequestDto {
        @NotNull(message = "La date de début est obligatoire")
        private LocalDate startDate;

        @NotNull(message = "La date de fin est obligatoire")
        private LocalDate endDate;

        private String reason;
    }

    @Getter
    @Setter
    static class TimeoffStatusUpdateDto {
        @NotNull(message = "Le statut est obligatoire")
        private TimeoffStatus status;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('view-hr')")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeTimeoffResponseDto>>> findAll(
            @PathVariable UUID employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                employeeTimeoffRepository.findByEmployeeId(employeeId,
                        PageRequest.of(page, size)).map(t -> new EmployeeTimeoffResponseDto(
                        t.getId(), t.getEmployee().getId(), t.getStartDate(),
                        t.getEndDate(), t.getReason(), t.getStatus(), t.getCreatedAt())))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-hr')")
    public ResponseEntity<ApiResponse<EmployeeTimeoffResponseDto>> findById(
            @PathVariable UUID employeeId,
            @PathVariable UUID id) {
        EmployeeTimeoff timeoff = employeeTimeoffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Congé", id));
        return ResponseEntity.ok(ApiResponse.success(new EmployeeTimeoffResponseDto(
                timeoff.getId(), timeoff.getEmployee().getId(), timeoff.getStartDate(),
                timeoff.getEndDate(), timeoff.getReason(), timeoff.getStatus(), timeoff.getCreatedAt())));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('manage-employees')")
    @Transactional
    public ResponseEntity<ApiResponse<EmployeeTimeoffResponseDto>> create(
            @PathVariable UUID employeeId,
            @Valid @RequestBody EmployeeTimeoffRequestDto dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", employeeId));
        EmployeeTimeoff timeoff = new EmployeeTimeoff();
        timeoff.setEmployee(employee);
        timeoff.setStartDate(dto.getStartDate());
        timeoff.setEndDate(dto.getEndDate());
        timeoff.setReason(dto.getReason());
        timeoff.setStatus(TimeoffStatus.PENDING);
        EmployeeTimeoff saved = employeeTimeoffRepository.save(timeoff);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Congé créé", new EmployeeTimeoffResponseDto(
                        saved.getId(), saved.getEmployee().getId(), saved.getStartDate(),
                        saved.getEndDate(), saved.getReason(), saved.getStatus(), saved.getCreatedAt())));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('manage-employees')")
    @Transactional
    public ResponseEntity<ApiResponse<EmployeeTimeoffResponseDto>> updateStatus(
            @PathVariable UUID employeeId,
            @PathVariable UUID id,
            @Valid @RequestBody TimeoffStatusUpdateDto dto) {
        EmployeeTimeoff timeoff = employeeTimeoffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Congé", id));

        TimeoffStatus previousStatus = timeoff.getStatus();
        timeoff.setStatus(dto.getStatus());
        EmployeeTimeoff saved = employeeTimeoffRepository.save(timeoff);

        // Cascade User.isActive lors de l'approbation ou de la révocation d'un congé
        Employee employee = timeoff.getEmployee();
        if (employee.getUser() != null) {
            User user = employee.getUser();
            if (dto.getStatus() == TimeoffStatus.APPROVED) {
                user.setActive(false);
                userRepository.save(user);
            } else if (previousStatus == TimeoffStatus.APPROVED
                    && (dto.getStatus() == TimeoffStatus.REJECTED
                        || dto.getStatus() == TimeoffStatus.PENDING)) {
                user.setActive(true);
                userRepository.save(user);
            }
        }

        return ResponseEntity.ok(ApiResponse.success("Statut mis à jour", new EmployeeTimeoffResponseDto(
                saved.getId(), saved.getEmployee().getId(), saved.getStartDate(),
                saved.getEndDate(), saved.getReason(), saved.getStatus(), saved.getCreatedAt())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-employees')")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID employeeId,
            @PathVariable UUID id) {
        EmployeeTimeoff timeoff = employeeTimeoffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Congé", id));
        if (timeoff.getStatus() == TimeoffStatus.APPROVED) {
            throw new InvalidOperationException("Impossible de supprimer un congé approuvé");
        }
        employeeTimeoffRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Congé supprimé", null));
    }
}
