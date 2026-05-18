package com.labo.anapath.doc;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/docs")
@RequiredArgsConstructor
public class DocController {

    private final DocService docService;

    @GetMapping
    @PreAuthorize("hasAuthority('view-documentation')")
    public ResponseEntity<ApiResponse<PageResponse<DocResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                docService.findAll(page, size, principal.getBranchId())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-documentation')")
    public ResponseEntity<ApiResponse<DocResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(docService.findById(id)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('manage-documentation')")
    public ResponseEntity<ApiResponse<DocResponseDto>> create(
            @RequestParam String title,
            @RequestParam(required = false) UUID documentationCategoryId,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Document créé",
                        docService.create(title, documentationCategoryId, file,
                                principal.getId(), principal.getBranchId())));
    }

    @PostMapping(value = "/{id}/versions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('manage-documentation')")
    public ResponseEntity<ApiResponse<DocVersionResponseDto>> addVersion(
            @PathVariable UUID id,
            @RequestParam(required = false) String title,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Version ajoutée",
                        docService.addVersion(id, title, file,
                                principal.getId(), principal.getBranchId())));
    }

    @GetMapping("/{id}/versions")
    @PreAuthorize("hasAuthority('view-documentation')")
    public ResponseEntity<ApiResponse<List<DocVersionResponseDto>>> getVersions(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(docService.getVersions(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-documentation')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        docService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Document supprimé", null));
    }
}
