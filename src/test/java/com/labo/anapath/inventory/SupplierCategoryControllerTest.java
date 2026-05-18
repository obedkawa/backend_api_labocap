package com.labo.anapath.inventory;

import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.common.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierCategoryControllerTest {

    @Mock SupplierCategoryRepository supplierCategoryRepository;
    @Mock SupplierRepository supplierRepository;

    SupplierCategoryController controller;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID CAT_ID    = UUID.randomUUID();

    @BeforeEach
    void setup() {
        controller = new SupplierCategoryController(supplierCategoryRepository, supplierRepository);
    }

    private UserPrincipal mockPrincipal() {
        UserPrincipal p = org.mockito.Mockito.mock(UserPrincipal.class);
        when(p.getBranchId()).thenReturn(BRANCH_ID);
        return p;
    }

    private SupplierCategory buildCategory() {
        SupplierCategory c = new SupplierCategory();
        ReflectionTestUtils.setField(c, "id", CAT_ID);
        c.setName("Réactifs");
        return c;
    }

    @Test
    @DisplayName("findAll - retourne liste de la branche")
    void findAll_returnsListByBranch() {
        SupplierCategory cat = buildCategory();
        when(supplierCategoryRepository.findByBranchId(BRANCH_ID)).thenReturn(List.of(cat));

        ResponseEntity<?> response = controller.findAll(mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("create - retourne 201")
    void createCategory_returns201() {
        SupplierCategory saved = buildCategory();
        when(supplierCategoryRepository.save(any())).thenReturn(saved);

        SupplierCategoryRequestDto dto = new SupplierCategoryRequestDto();
        dto.setName("Réactifs");
        dto.setDescription("Réactifs chimiques");

        ResponseEntity<?> response = controller.create(dto, mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("update - ID inconnu → ResourceNotFoundException")
    void updateCategory_notFound_throws() {
        when(supplierCategoryRepository.findById(CAT_ID)).thenReturn(Optional.empty());

        SupplierCategoryRequestDto dto = new SupplierCategoryRequestDto();
        dto.setName("Updated");

        assertThatThrownBy(() -> controller.update(CAT_ID, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - modifie name et description → 200")
    void updateCategory_updatesAndReturns200() {
        SupplierCategory cat = buildCategory();
        when(supplierCategoryRepository.findById(CAT_ID)).thenReturn(Optional.of(cat));
        when(supplierCategoryRepository.save(any())).thenReturn(cat);

        SupplierCategoryRequestDto dto = new SupplierCategoryRequestDto();
        dto.setName("Matériel médical");
        dto.setDescription("Description mise à jour");

        ResponseEntity<?> response = controller.update(CAT_ID, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cat.getName()).isEqualTo("Matériel médical");
    }

    @Test
    @DisplayName("delete - catégorie liée à des fournisseurs → InvalidOperationException")
    void deleteCategory_linkedToSuppliers_throws() {
        SupplierCategory cat = buildCategory();
        when(supplierCategoryRepository.findById(CAT_ID)).thenReturn(Optional.of(cat));
        when(supplierRepository.existsBySupplierCategory(cat)).thenReturn(true);

        assertThatThrownBy(() -> controller.delete(CAT_ID))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    @DisplayName("delete - catégorie sans fournisseurs → 200")
    void deleteCategory_noLinkedSuppliers_returns200() {
        SupplierCategory cat = buildCategory();
        when(supplierCategoryRepository.findById(CAT_ID)).thenReturn(Optional.of(cat));
        when(supplierRepository.existsBySupplierCategory(cat)).thenReturn(false);

        ResponseEntity<?> response = controller.delete(CAT_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(supplierCategoryRepository).delete(cat);
    }
}
