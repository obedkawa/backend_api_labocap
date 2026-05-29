package com.labo.anapath.doc;

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
@RequestMapping("/api/v1/documentation-categories")
@RequiredArgsConstructor
public class DocumentationCategoryController {

    private final DocumentationCategoryService documentationCategoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-documentation-categories')")
    public ResponseEntity<ApiResponse<List<DocumentationCategoryResponseDto>>> findAll(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                documentationCategoryService.findAll(principal.getBranchId())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-documentation-categories')")
    public ResponseEntity<ApiResponse<DocumentationCategoryResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(documentationCategoryService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('edit-documentation-categories')")
    public ResponseEntity<ApiResponse<DocumentationCategoryResponseDto>> create(
            @Valid @RequestBody DocumentationCategoryRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Catégorie créée",
                        documentationCategoryService.create(dto, principal.getBranchId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-documentation-categories')")
    public ResponseEntity<ApiResponse<DocumentationCategoryResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody DocumentationCategoryRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Catégorie mise à jour",
                documentationCategoryService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-documentation-categories')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        documentationCategoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Catégorie supprimée", null));
    }
}
