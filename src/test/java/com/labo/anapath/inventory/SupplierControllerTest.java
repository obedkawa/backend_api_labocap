package com.labo.anapath.inventory;

import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.common.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierControllerTest {

    @Mock SupplierRepository supplierRepository;
    @Mock SupplierCategoryRepository supplierCategoryRepository;

    SupplierController controller;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID SUP_ID    = UUID.randomUUID();

    @BeforeEach
    void setup() {
        controller = new SupplierController(supplierRepository, supplierCategoryRepository);
    }

    private UserPrincipal mockPrincipal() {
        UserPrincipal p = org.mockito.Mockito.mock(UserPrincipal.class);
        when(p.getBranchId()).thenReturn(BRANCH_ID);
        return p;
    }

    private Supplier buildSupplier() {
        Supplier s = new Supplier();
        ReflectionTestUtils.setField(s, "id", SUP_ID);
        s.setName("Pharmac");
        return s;
    }

    @Test
    @DisplayName("findAll - retourne page paginée par branche")
    void findAll_returnsPaginatedByBranch() {
        Supplier s = buildSupplier();
        Page<Supplier> page = new PageImpl<>(List.of(s));
        when(supplierRepository.findByBranchId(eq(BRANCH_ID), any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = controller.findAll(0, 20, mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("search - retourne liste filtrée par nom")
    void search_returnsMatchingResults() {
        Supplier s = buildSupplier();
        when(supplierRepository.findByBranchIdAndNameContainingIgnoreCase(BRANCH_ID, "Phar"))
                .thenReturn(List.of(s));

        ResponseEntity<?> response = controller.search("Phar", mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("findById - ID existant → 200")
    void findById_returns200() {
        Supplier s = buildSupplier();
        when(supplierRepository.findById(SUP_ID)).thenReturn(Optional.of(s));

        ResponseEntity<?> response = controller.findById(SUP_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("findById - ID inconnu → ResourceNotFoundException")
    void findById_unknownId_throws() {
        when(supplierRepository.findById(SUP_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.findById(SUP_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("create - sans categoryId → 201")
    void create_withoutCategory_returns201() {
        Supplier saved = buildSupplier();
        when(supplierRepository.save(any())).thenReturn(saved);

        SupplierRequestDto dto = new SupplierRequestDto();
        dto.setName("Pharmac");
        dto.setPhone("97000000");

        ResponseEntity<?> response = controller.create(dto, mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("create - avec categoryId inconnu → ResourceNotFoundException")
    void create_withUnknownCategoryId_throws() {
        UUID catId = UUID.randomUUID();
        when(supplierCategoryRepository.findById(catId)).thenReturn(Optional.empty());

        SupplierRequestDto dto = new SupplierRequestDto();
        dto.setName("Bio Med");
        dto.setCategoryId(catId);

        assertThatThrownBy(() -> controller.create(dto, mockPrincipal()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete - fournisseur existant → 200")
    void delete_returns200() {
        Supplier s = buildSupplier();
        when(supplierRepository.findById(SUP_ID)).thenReturn(Optional.of(s));

        ResponseEntity<?> response = controller.delete(SUP_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
