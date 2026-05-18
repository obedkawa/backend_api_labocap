package com.labo.anapath.setting;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/setting-invoices")
@RequiredArgsConstructor
public class SettingInvoiceController {

    private final SettingInvoiceService settingInvoiceService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-settings')")
    public ResponseEntity<ApiResponse<PageResponse<SettingInvoiceResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                settingInvoiceService.findAll(page, size, principal.getBranchId())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-settings')")
    public ResponseEntity<ApiResponse<SettingInvoiceResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(settingInvoiceService.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-settings')")
    public ResponseEntity<ApiResponse<SettingInvoiceResponseDto>> update(
            @PathVariable UUID id,
            @RequestBody SettingInvoiceRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Configuration MECeF mise à jour",
                settingInvoiceService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-settings')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        settingInvoiceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Configuration MECeF supprimée", null));
    }
}
