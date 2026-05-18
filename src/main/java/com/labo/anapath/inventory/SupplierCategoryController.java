package com.labo.anapath.inventory;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/api/v1/supplier-categories")
@RequiredArgsConstructor
public class SupplierCategoryController {

    private final SupplierCategoryRepository supplierCategoryRepository;
    private final SupplierRepository supplierRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('view-inventory')")
    public ResponseEntity<ApiResponse<List<SupplierCategoryResponseDto>>> findAll(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<SupplierCategoryResponseDto> result = supplierCategoryRepository
                .findByBranchId(principal.getBranchId())
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('manage-inventory')")
    @Transactional
    public ResponseEntity<ApiResponse<SupplierCategoryResponseDto>> create(
            @Valid @RequestBody SupplierCategoryRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        SupplierCategory category = new SupplierCategory();
        category.setBranchId(principal.getBranchId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        SupplierCategory saved = supplierCategoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Catégorie créée", toDto(saved)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-inventory')")
    @Transactional
    public ResponseEntity<ApiResponse<SupplierCategoryResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody SupplierCategoryRequestDto dto) {
        SupplierCategory category = supplierCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie fournisseur", id));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return ResponseEntity.ok(ApiResponse.success("Catégorie mise à jour",
                toDto(supplierCategoryRepository.save(category))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-inventory')")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        SupplierCategory category = supplierCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie fournisseur", id));
        boolean hasLinkedSuppliers = supplierRepository.existsBySupplierCategory(category);
        if (hasLinkedSuppliers) {
            throw new InvalidOperationException(
                    "Impossible de supprimer une catégorie liée à des fournisseurs");
        }
        supplierCategoryRepository.delete(category);
        return ResponseEntity.ok(ApiResponse.success("Catégorie supprimée", null));
    }

    private SupplierCategoryResponseDto toDto(SupplierCategory c) {
        return new SupplierCategoryResponseDto(
                c.getId(), c.getName(), c.getDescription(), c.getBranchId(), c.getCreatedAt());
    }
}
