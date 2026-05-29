package com.labo.anapath.inventory;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.PageImpl;
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
class SupplierControllerTest {

    @Mock SupplierService supplierService;

    SupplierController controller;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID SUP_ID    = UUID.randomUUID();

    @BeforeEach
    void setup() {
        controller = new SupplierController(supplierService);
    }

    private UserPrincipal mockPrincipal() {
        UserPrincipal p = org.mockito.Mockito.mock(UserPrincipal.class);
        when(p.getBranchId()).thenReturn(BRANCH_ID);
        return p;
    }

    private SupplierResponseDto dummyDto() {
        return new SupplierResponseDto(SUP_ID, "Pharmac", null, null, null, null, null, null, null, BRANCH_ID, null);
    }

    @Test
    @DisplayName("findAll - retourne page paginée par branche")
    void findAll_returnsPaginatedByBranch() {
        when(supplierService.findAll(0, 20, BRANCH_ID))
                .thenReturn(PageResponse.of(new PageImpl<>(List.of())));

        ResponseEntity<?> response = controller.findAll(0, 20, mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("search - retourne liste filtrée par nom")
    void search_returnsMatchingResults() {
        when(supplierService.search("Phar", BRANCH_ID)).thenReturn(List.of(dummyDto()));

        ResponseEntity<?> response = controller.search("Phar", mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("findById - ID existant → 200")
    void findById_returns200() {
        when(supplierService.findById(SUP_ID)).thenReturn(dummyDto());

        ResponseEntity<?> response = controller.findById(SUP_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("findById - ID inconnu → ResourceNotFoundException")
    void findById_unknownId_throws() {
        when(supplierService.findById(SUP_ID))
                .thenThrow(new ResourceNotFoundException("Fournisseur", SUP_ID));

        assertThatThrownBy(() -> controller.findById(SUP_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("create - sans categoryId → 201")
    void create_returns201() {
        when(supplierService.create(any(), eq(BRANCH_ID))).thenReturn(dummyDto());

        SupplierRequestDto dto = new SupplierRequestDto();
        dto.setName("Pharmac");
        dto.setPhone("97000000");

        ResponseEntity<?> response = controller.create(dto, mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("create - avec categoryId inconnu → ResourceNotFoundException propagée depuis le service")
    void create_withUnknownCategoryId_throws() {
        UUID catId = UUID.randomUUID();
        SupplierRequestDto dto = new SupplierRequestDto();
        dto.setName("Bio Med");
        dto.setCategoryId(catId);

        when(supplierService.create(any(), eq(BRANCH_ID)))
                .thenThrow(new ResourceNotFoundException("Catégorie fournisseur", catId));

        assertThatThrownBy(() -> controller.create(dto, mockPrincipal()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete - fournisseur existant → 200")
    void delete_returns200() {
        ResponseEntity<?> response = controller.delete(SUP_ID);

        verify(supplierService).delete(SUP_ID);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("delete - ID inconnu → ResourceNotFoundException propagée depuis le service")
    void delete_unknownId_throws() {
        doThrow(new ResourceNotFoundException("Fournisseur", SUP_ID))
                .when(supplierService).delete(SUP_ID);

        assertThatThrownBy(() -> controller.delete(SUP_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
