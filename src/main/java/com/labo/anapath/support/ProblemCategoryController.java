package com.labo.anapath.support;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/problem-categories")
@RequiredArgsConstructor
public class ProblemCategoryController {

    private final ProblemCategoryService problemCategoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-support')")
    public ResponseEntity<ApiResponse<List<ProblemCategoryResponseDto>>> findAll(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                problemCategoryService.findAll(principal.getBranchId())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-support')")
    public ResponseEntity<ApiResponse<ProblemCategoryResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(problemCategoryService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('manage-support')")
    public ResponseEntity<ApiResponse<ProblemCategoryResponseDto>> create(
            @Valid @RequestBody ProblemCategoryRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Catégorie créée",
                        problemCategoryService.create(dto, principal.getBranchId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-support')")
    public ResponseEntity<ApiResponse<ProblemCategoryResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProblemCategoryRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Catégorie mise à jour",
                problemCategoryService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-support')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        problemCategoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Catégorie supprimée", null));
    }
}
