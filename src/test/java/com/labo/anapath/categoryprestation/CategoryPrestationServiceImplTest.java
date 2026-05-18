package com.labo.anapath.categoryprestation;

import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryPrestationServiceImplTest {

    @Mock private CategoryPrestationRepository repository;
    @InjectMocks private CategoryPrestationServiceImpl service;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID CAT_ID = UUID.randomUUID();

    @Test
    @DisplayName("create - génère un slug à partir du nom")
    void create_shouldGenerateSlugFromName() {
        CategoryPrestationRequestDto dto = new CategoryPrestationRequestDto();
        dto.setName("Analyses Biochimiques");

        CategoryPrestation saved = new CategoryPrestation();
        saved.setId(CAT_ID);
        saved.setBranchId(BRANCH_ID);
        saved.setName("Analyses Biochimiques");
        saved.setSlug("analyses-biochimiques");

        when(repository.save(any())).thenReturn(saved);

        CategoryPrestationResponseDto result = service.create(dto, BRANCH_ID);

        assertThat(result.slug()).isEqualTo("analyses-biochimiques");
        assertThat(result.name()).isEqualTo("Analyses Biochimiques");
    }

    @Test
    @DisplayName("create - slug sans caractères spéciaux")
    void create_slugStripsSpecialChars() {
        CategoryPrestationRequestDto dto = new CategoryPrestationRequestDto();
        dto.setName("Sérologie & Immunologie");

        CategoryPrestation saved = new CategoryPrestation();
        saved.setId(CAT_ID);
        saved.setBranchId(BRANCH_ID);
        saved.setName("Sérologie & Immunologie");
        saved.setSlug("srologie--immunologie");

        when(repository.save(any())).thenReturn(saved);

        CategoryPrestationResponseDto result = service.create(dto, BRANCH_ID);

        assertThat(result.slug()).isNotBlank();
    }

    @Test
    @DisplayName("delete - lève InvalidOperationException si catégorie a des prestations")
    void delete_shouldThrowInvalidOperation_whenPrestationsExist() {
        CategoryPrestation cat = new CategoryPrestation();
        cat.setId(CAT_ID);
        com.labo.anapath.prestation.Prestation prestation = new com.labo.anapath.prestation.Prestation();
        cat.getPrestations().add(prestation);

        when(repository.findById(CAT_ID)).thenReturn(Optional.of(cat));

        assertThatThrownBy(() -> service.delete(CAT_ID))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("catégorie contient des prestations");
    }

    @Test
    @DisplayName("delete - catégorie introuvable → ResourceNotFoundException")
    void delete_categoryNotFound_throwsResourceNotFoundException() {
        when(repository.findById(CAT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(CAT_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete - catégorie sans prestations → suppression OK")
    void delete_noPrestations_deletesSuccessfully() {
        CategoryPrestation cat = new CategoryPrestation();
        cat.setId(CAT_ID);

        when(repository.findById(CAT_ID)).thenReturn(Optional.of(cat));

        service.delete(CAT_ID);

        verify(repository).delete(cat);
    }

    @Test
    @DisplayName("update - met à jour nom et régénère le slug")
    void update_updatesNameAndSlug() {
        CategoryPrestation existing = new CategoryPrestation();
        existing.setId(CAT_ID);
        existing.setName("Ancien Nom");
        existing.setSlug("ancien-nom");

        CategoryPrestationRequestDto dto = new CategoryPrestationRequestDto();
        dto.setName("Nouveau Nom");

        CategoryPrestation saved = new CategoryPrestation();
        saved.setId(CAT_ID);
        saved.setName("Nouveau Nom");
        saved.setSlug("nouveau-nom");

        when(repository.findById(CAT_ID)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(saved);

        CategoryPrestationResponseDto result = service.update(CAT_ID, dto);

        assertThat(result.name()).isEqualTo("Nouveau Nom");
        assertThat(result.slug()).isEqualTo("nouveau-nom");
    }
}
