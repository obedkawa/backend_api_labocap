package com.labo.anapath.setting;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/setting-apps")
@RequiredArgsConstructor
public class SettingAppController {

    private final SettingAppService settingAppService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-settings')")
    public ResponseEntity<ApiResponse<PageResponse<SettingAppResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                settingAppService.findAll(page, size, principal.getBranchId())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-settings')")
    public ResponseEntity<ApiResponse<SettingAppResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(settingAppService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('edit-settings')")
    public ResponseEntity<ApiResponse<SettingAppResponseDto>> upsert(
            @Valid @RequestBody SettingAppRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Paramètre sauvegardé",
                settingAppService.upsert(dto, principal.getBranchId())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-settings')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        settingAppService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Paramètre supprimé", null));
    }
}
