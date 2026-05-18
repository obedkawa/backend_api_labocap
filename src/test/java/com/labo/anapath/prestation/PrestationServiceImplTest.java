package com.labo.anapath.prestation;

import com.labo.anapath.categoryprestation.CategoryPrestation;
import com.labo.anapath.categoryprestation.CategoryPrestationRepository;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.prestationorder.PrestationOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestationServiceImplTest {

    @Mock private PrestationRepository repository;
    @Mock private CategoryPrestationRepository categoryRepository;
    @Mock private PrestationOrderRepository prestationOrderRepository;
    @Mock private PrestationMapper mapper;

    @InjectMocks private PrestationServiceImpl service;

    private final UUID BRANCH_ID   = UUID.randomUUID();
    private final UUID PRESTATION_ID = UUID.randomUUID();
    private final UUID CATEGORY_ID = UUID.randomUUID();

    // ------------------------------------------------------------------ create

    @Test
    @DisplayName("create - categoryId valide → retourne PrestationResponseDto")
    void create_success_returnsPrestationDto() {
        PrestationRequestDto dto = new PrestationRequestDto();
        dto.setName("Numération Formule Sanguine");
        dto.setPrice(new BigDecimal("5000.00"));
        dto.setDescription("Analyse hématologique complète");
        dto.setCategoryPrestationId(CATEGORY_ID);

        CategoryPrestation category = new CategoryPrestation();
        category.setId(CATEGORY_ID);
        category.setName("Hématologie");

        Prestation saved = new Prestation();
        saved.setName(dto.getName());
        saved.setPrice(dto.getPrice());
        saved.setDescription(dto.getDescription());
        saved.setCategoryPrestation(category);

        PrestationResponseDto expectedDto = new PrestationResponseDto(
                PRESTATION_ID, dto.getName(), dto.getPrice(), dto.getDescription(),
                CATEGORY_ID, "Hématologie", BRANCH_ID, null);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(repository.save(any(Prestation.class))).thenReturn(saved);
        when(mapper.toResponseDto(saved)).thenReturn(expectedDto);

        PrestationResponseDto result = service.create(dto, BRANCH_ID);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Numération Formule Sanguine");
        assertThat(result.categoryPrestationId()).isEqualTo(CATEGORY_ID);
        verify(repository).save(any(Prestation.class));
    }

    @Test
    @DisplayName("create - categoryId inexistant → ResourceNotFoundException")
    void create_categoryNotFound_throws404() {
        PrestationRequestDto dto = new PrestationRequestDto();
        dto.setName("Test Prestation");
        dto.setPrice(new BigDecimal("1000.00"));
        dto.setCategoryPrestationId(CATEGORY_ID);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto, BRANCH_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ------------------------------------------------------------------ delete

    @Test
    @DisplayName("delete - prestation non utilisée → soft delete OK")
    void delete_success() {
        Prestation prestation = new Prestation();
        prestation.setName("Glycémie");
        prestation.setPrice(new BigDecimal("2000.00"));

        when(repository.findById(PRESTATION_ID)).thenReturn(Optional.of(prestation));
        when(prestationOrderRepository.existsByPrestationId(PRESTATION_ID)).thenReturn(false);

        service.delete(PRESTATION_ID);

        verify(repository).deleteById(PRESTATION_ID);
    }

    @Test
    @DisplayName("delete - prestation utilisée par des orders → InvalidOperationException")
    void delete_usedByConsultations_throws422() {
        Prestation prestation = new Prestation();
        prestation.setName("Glycémie");

        when(repository.findById(PRESTATION_ID)).thenReturn(Optional.of(prestation));
        when(prestationOrderRepository.existsByPrestationId(PRESTATION_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(PRESTATION_ID))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("prestation est utilisée");
    }

    @Test
    @DisplayName("delete - prestation introuvable → ResourceNotFoundException")
    void delete_notFound_throwsResourceNotFoundException() {
        when(repository.findById(PRESTATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(PRESTATION_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
