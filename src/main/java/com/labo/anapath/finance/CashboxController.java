package com.labo.anapath.finance;

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
@RequestMapping("/api/v1/cashboxes")
@RequiredArgsConstructor
public class CashboxController {

    private final CashboxService cashboxService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-finance')")
    public ResponseEntity<ApiResponse<PageResponse<CashboxResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                cashboxService.findAll(page, size, principal.getBranchId())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-finance')")
    public ResponseEntity<ApiResponse<CashboxResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(cashboxService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('manage-finance')")
    public ResponseEntity<ApiResponse<CashboxResponseDto>> create(
            @Valid @RequestBody CashboxRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Caisse créée",
                        cashboxService.create(dto, principal.getBranchId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-finance')")
    public ResponseEntity<ApiResponse<CashboxResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CashboxRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Caisse mise à jour",
                cashboxService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-finance')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        cashboxService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Caisse supprimée", null));
    }
}
