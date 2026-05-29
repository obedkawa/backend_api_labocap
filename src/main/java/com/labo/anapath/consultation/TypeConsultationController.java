package com.labo.anapath.consultation;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/type-consultations")
@RequiredArgsConstructor
public class TypeConsultationController {

    private final TypeConsultationService typeConsultationService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-consultations')")
    public ResponseEntity<ApiResponse<PageResponse<TypeConsultationResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                typeConsultationService.findAll(page, size, principal.getBranchId())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-consultations')")
    public ResponseEntity<ApiResponse<TypeConsultationResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(typeConsultationService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create-type-consultations')")
    public ResponseEntity<ApiResponse<TypeConsultationResponseDto>> create(
            @Valid @RequestBody TypeConsultationRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Type créé", typeConsultationService.create(dto, principal.getBranchId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-type-consultations')")
    public ResponseEntity<ApiResponse<TypeConsultationResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody TypeConsultationRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Type mis à jour", typeConsultationService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete-type-consultations')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        typeConsultationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Type supprimé", null));
    }
}
