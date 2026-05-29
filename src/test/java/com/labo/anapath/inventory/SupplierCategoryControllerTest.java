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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierCategoryControllerTest {

    @Mock SupplierCategoryService supplierCategoryService;

    SupplierCategoryController controller;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID CAT_ID    = UUID.randomUUID();

    @BeforeEach
    void setup() {
        controller = new SupplierCategoryController(supplierCategoryService);
    }

    private UserPrincipal mockPrincipal() {
        UserPrincipal p = org.mockito.Mockito.mock(UserPrincipal.class);
        when(p.getBranchId()).thenReturn(BRANCH_ID);
        return p;
    }

    private SupplierCategoryResponseDto dummyDto() {
        return new SupplierCategoryResponseDto(CAT_ID, "Réactifs", "Réactifs chimiques", BRANCH_ID, null);
    }

    @Test
    @DisplayName("findAll - retourne liste de la branche")
    void findAll_returnsListByBranch() {
        when(supplierCategoryService.findAll(BRANCH_ID)).thenReturn(List.of(dummyDto()));

        ResponseEntity<?> response = controller.findAll(mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("create - retourne 201")
    void createCategory_returns201() {
        when(supplierCategoryService.create(any(), eq(BRANCH_ID))).thenReturn(dummyDto());

        SupplierCategoryRequestDto dto = new SupplierCategoryRequestDto();
        dto.setName("Réactifs");
        dto.setDescription("Réactifs chimiques");

        ResponseEntity<?> response = controller.create(dto, mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("update - ID inconnu → ResourceNotFoundException propagée depuis le service")
    void updateCategory_notFound_throws() {
        when(supplierCategoryService.update(eq(CAT_ID), any()))
                .thenThrow(new ResourceNotFoundException("Catégorie fournisseur", CAT_ID));

        SupplierCategoryRequestDto dto = new SupplierCategoryRequestDto();
        dto.setName("Updated");

        assertThatThrownBy(() -> controller.update(CAT_ID, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - modifie name et description → 200")
    void updateCategory_updatesAndReturns200() {
        SupplierCategoryResponseDto updated = new SupplierCategoryResponseDto(
                CAT_ID, "Matériel médical", "Description mise à jour", BRANCH_ID, null);
        when(supplierCategoryService.update(eq(CAT_ID), any())).thenReturn(updated);

        SupplierCategoryRequestDto dto = new SupplierCategoryRequestDto();
        dto.setName("Matériel médical");
        dto.setDescription("Description mise à jour");

        ResponseEntity<?> response = controller.update(CAT_ID, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("delete - catégorie liée à des fournisseurs → InvalidOperationException propagée")
    void deleteCategory_linkedToSuppliers_throws() {
        doThrow(new InvalidOperationException("Impossible de supprimer une catégorie liée à des fournisseurs"))
                .when(supplierCategoryService).delete(CAT_ID);

        assertThatThrownBy(() -> controller.delete(CAT_ID))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    @DisplayName("delete - catégorie sans fournisseurs → 200")
    void deleteCategory_noLinkedSuppliers_returns200() {
        ResponseEntity<?> response = controller.delete(CAT_ID);

        verify(supplierCategoryService).delete(CAT_ID);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
