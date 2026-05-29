package com.labo.anapath.support;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/problem-reports")
@RequiredArgsConstructor
public class ProblemReportController {

    private final ProblemReportService problemReportService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-tickets')")
    public ResponseEntity<ApiResponse<PageResponse<ProblemReportResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                problemReportService.findAll(page, size, principal.getBranchId())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-tickets')")
    public ResponseEntity<ApiResponse<ProblemReportResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(problemReportService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('view-tickets')")
    public ResponseEntity<ApiResponse<ProblemReportResponseDto>> create(
            @Valid @RequestBody ProblemReportRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Signalement créé",
                        problemReportService.create(dto, principal.getBranchId())));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('edit-tickets')")
    public ResponseEntity<ApiResponse<ProblemReportResponseDto>> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Statut mis à jour",
                problemReportService.updateStatus(id, status)));
    }
}
