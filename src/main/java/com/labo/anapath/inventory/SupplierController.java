package com.labo.anapath.inventory;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierRepository supplierRepository;
    private final SupplierCategoryRepository supplierCategoryRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('view-inventory')")
    public ResponseEntity<ApiResponse<PageResponse<SupplierResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                supplierRepository.findByBranchId(principal.getBranchId(), PageRequest.of(page, size))
                        .map(this::toDto))));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('view-inventory')")
    public ResponseEntity<ApiResponse<List<SupplierResponseDto>>> search(
            @RequestParam String q,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<SupplierResponseDto> result = supplierRepository
                .findByBranchIdAndNameContainingIgnoreCase(principal.getBranchId(), q)
                .stream().map(this::toDto).toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-inventory')")
    public ResponseEntity<ApiResponse<SupplierResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                toDto(supplierRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id)))));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('manage-inventory')")
    @Transactional
    public ResponseEntity<ApiResponse<SupplierResponseDto>> create(
            @Valid @RequestBody SupplierRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        Supplier supplier = new Supplier();
        supplier.setBranchId(principal.getBranchId());
        applyDto(dto, supplier);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fournisseur créé", toDto(supplierRepository.save(supplier))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-inventory')")
    @Transactional
    public ResponseEntity<ApiResponse<SupplierResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody SupplierRequestDto dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
        applyDto(dto, supplier);
        return ResponseEntity.ok(ApiResponse.success("Fournisseur mis à jour",
                toDto(supplierRepository.save(supplier))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-inventory')")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
        supplierRepository.delete(supplier);
        return ResponseEntity.ok(ApiResponse.success("Fournisseur supprimé", null));
    }

    private void applyDto(SupplierRequestDto dto, Supplier supplier) {
        supplier.setName(dto.getName());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        supplier.setCategory(dto.getCategory());
        if (dto.getCategoryId() != null) {
            supplier.setSupplierCategory(
                    supplierCategoryRepository.findById(dto.getCategoryId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Catégorie fournisseur", dto.getCategoryId())));
        } else {
            supplier.setSupplierCategory(null);
        }
    }

    private SupplierResponseDto toDto(Supplier s) {
        UUID catId = s.getSupplierCategory() != null ? s.getSupplierCategory().getId() : null;
        String catName = s.getSupplierCategory() != null ? s.getSupplierCategory().getName() : null;
        return new SupplierResponseDto(
                s.getId(), s.getName(), s.getPhone(), s.getEmail(),
                s.getAddress(), s.getCategory(), catId, catName,
                s.getBranchId(), s.getCreatedAt());
    }
}
