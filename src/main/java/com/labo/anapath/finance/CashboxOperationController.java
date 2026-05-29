package com.labo.anapath.finance;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cashbox-operations")
@RequiredArgsConstructor
public class CashboxOperationController {

    private final CashboxOperationService cashboxOperationService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-invoices')")
    public ResponseEntity<ApiResponse<PageResponse<CashboxOperationResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID cashboxId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                cashboxOperationService.findAll(page, size, principal.getBranchId(), cashboxId, type, date)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('edit-invoices')")
    public ResponseEntity<ApiResponse<CashboxOperationResponseDto>> create(
            @Valid @RequestBody CashboxOperationCreateDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Opération enregistrée",
                        cashboxOperationService.create(dto, principal.getBranchId())));
    }
}
