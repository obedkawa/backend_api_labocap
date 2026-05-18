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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final MecefService mecefService;
    private final InvoiceRepository invoiceRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('view-finance')")
    public ResponseEntity<ApiResponse<PageResponse<InvoiceResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.findAll(page, size, principal.getBranchId())));
    }

    @GetMapping("/business")
    @PreAuthorize("hasAuthority('view-finance')")
    public ResponseEntity<ApiResponse<BusinessDashboardDto>> getBusiness(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getBusinessDashboard(principal.getBranchId())));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('view-finance')")
    public ResponseEntity<ApiResponse<InvoiceSearchResultDto>> searchByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceService.searchByPeriod(startDate, endDate, principal.getBranchId())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-finance')")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('manage-invoices')")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> create(
            @Valid @RequestBody InvoiceRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Facture créée", invoiceService.create(dto, principal.getBranchId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-invoices')")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody InvoiceRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Facture mise à jour", invoiceService.update(id, dto)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('manage-invoices')")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> markAsPaid(
            @PathVariable UUID id,
            @Valid @RequestBody InvoiceStatusUpdateDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Facture payée", invoiceService.markAsPaid(id, dto)));
    }

    @PostMapping("/{id}/confirm-mecef")
    @PreAuthorize("hasAuthority('manage-invoices')")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> confirmMecef(
            @PathVariable UUID id,
            @Valid @RequestBody MecefConfirmRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Facture normalisée MECeF",
                mecefService.confirmInvoice(id, dto.getUid(), principal.getBranchId())));
    }

    @PostMapping("/{id}/cancel-mecef")
    @PreAuthorize("hasAuthority('manage-invoices')")
    public ResponseEntity<ApiResponse<Void>> cancelMecef(
            @PathVariable UUID id,
            @Valid @RequestBody MecefConfirmRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        mecefService.cancelInvoice(id, dto.getUid(), principal.getBranchId());
        return ResponseEntity.ok(ApiResponse.success("Annulation MECeF effectuée", null));
    }

    @GetMapping("/check-code")
    @PreAuthorize("hasAuthority('view-finance')")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkCode(@RequestParam String code) {
        boolean exists = invoiceRepository.findFirstByCodeMecefOrCodeNormalise(code, code).isPresent();
        return ResponseEntity.ok(ApiResponse.success(Map.of("exists", exists)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-invoices')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        invoiceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Facture supprimée", null));
    }
}
