package com.labo.anapath.support;

import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemCategoryServiceTest {

    @Mock ProblemCategoryRepository problemCategoryRepository;
    @Mock ProblemCategoryMapper problemCategoryMapper;

    ProblemCategoryServiceImpl service;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID CAT_ID    = UUID.randomUUID();

    @BeforeEach
    void setup() {
        service = new ProblemCategoryServiceImpl(problemCategoryRepository, problemCategoryMapper);
    }

    private ProblemCategory buildCategory(String name) {
        ProblemCategory cat = new ProblemCategory();
        ReflectionTestUtils.setField(cat, "id", CAT_ID);
        cat.setName(name);
        return cat;
    }

    @Test
    @DisplayName("create - nom en doublon → InvalidOperationException")
    void create_duplicateName_throwsException() {
        when(problemCategoryRepository.findByNameAndBranchId("Facturation", BRANCH_ID))
                .thenReturn(Optional.of(buildCategory("Facturation")));

        ProblemCategoryRequestDto dto = new ProblemCategoryRequestDto();
        dto.setName("Facturation");

        assertThatThrownBy(() -> service.create(dto, BRANCH_ID))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    @DisplayName("create - nouveau nom → crée la catégorie")
    void create_newName_createsCategory() {
        when(problemCategoryRepository.findByNameAndBranchId("Résultat", BRANCH_ID))
                .thenReturn(Optional.empty());
        ProblemCategory saved = buildCategory("Résultat");
        when(problemCategoryRepository.save(any())).thenReturn(saved);
        when(problemCategoryMapper.toResponseDto(saved))
                .thenReturn(new ProblemCategoryResponseDto(CAT_ID, "Résultat", BRANCH_ID));

        ProblemCategoryRequestDto dto = new ProblemCategoryRequestDto();
        dto.setName("Résultat");

        var result = service.create(dto, BRANCH_ID);

        org.assertj.core.api.Assertions.assertThat(result.name()).isEqualTo("Résultat");
    }

    @Test
    @DisplayName("delete - ID inconnu → ResourceNotFoundException")
    void delete_notFound_throwsException() {
        when(problemCategoryRepository.findById(CAT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(CAT_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
