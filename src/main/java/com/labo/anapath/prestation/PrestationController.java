package com.labo.anapath.prestation;

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
@RequestMapping("/api/v1/prestations")
@RequiredArgsConstructor
public class PrestationController {

    private final PrestationService service;

    @GetMapping
    @PreAuthorize("hasAuthority('view-prestations')")
    public ResponseEntity<ApiResponse<PageResponse<PrestationResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID categoryId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                service.findAll(page, size, principal.getBranchId(), categoryId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-prestations')")
    public ResponseEntity<ApiResponse<PrestationResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create-prestations')")
    public ResponseEntity<ApiResponse<PrestationResponseDto>> create(
            @Valid @RequestBody PrestationRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Prestation créée", service.create(dto, principal.getBranchId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-prestations')")
    public ResponseEntity<ApiResponse<PrestationResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody PrestationRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Prestation mise à jour", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete-prestations')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Prestation supprimée", null));
    }
}
