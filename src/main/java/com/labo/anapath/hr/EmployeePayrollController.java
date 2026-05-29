package com.labo.anapath.hr;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees/{employeeId}/payrolls")
@RequiredArgsConstructor
public class EmployeePayrollController {

    private final EmployeePayrollService employeePayrollService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-employees')")
    public ResponseEntity<ApiResponse<PageResponse<EmployeePayrollResponseDto>>> findAll(
            @PathVariable UUID employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(employeePayrollService.findAll(page, size, employeeId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('edit-employees')")
    public ResponseEntity<ApiResponse<EmployeePayrollResponseDto>> create(
            @PathVariable UUID employeeId,
            @Valid @RequestBody EmployeePayrollRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fiche de paie créée", employeePayrollService.create(dto, employeeId)));
    }
}
