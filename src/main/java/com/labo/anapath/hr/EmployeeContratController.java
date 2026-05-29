package com.labo.anapath.hr;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees/{employeeId}/contrats")
@RequiredArgsConstructor
public class EmployeeContratController {

    private final EmployeeContratService employeeContratService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-employees')")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeContratResponseDto>>> findAll(
            @PathVariable UUID employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(employeeContratService.findAll(page, size, employeeId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('edit-employees')")
    public ResponseEntity<ApiResponse<EmployeeContratResponseDto>> create(
            @PathVariable UUID employeeId,
            @Valid @RequestBody EmployeeContratRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contrat créé", employeeContratService.create(dto, employeeId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-employees')")
    public ResponseEntity<ApiResponse<EmployeeContratResponseDto>> update(
            @PathVariable UUID employeeId,
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeContratRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Contrat mis à jour", employeeContratService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-employees')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID employeeId, @PathVariable UUID id) {
        employeeContratService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Contrat supprimé", null));
    }
}
