package com.labo.anapath.test;

import com.labo.anapath.common.exception.DuplicateResourceException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeOrderServiceImplTest {

    @Mock
    private TypeOrderRepository typeOrderRepository;

    @Mock
    private TestCatalogueMapper mapper;

    @InjectMocks
    private TypeOrderServiceImpl typeOrderService;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID ID = UUID.randomUUID();

    private TypeOrder buildEntity(String title, String slug) {
        TypeOrder to = new TypeOrder();
        to.setTitle(title);
        to.setSlug(slug);
        to.setBranchId(BRANCH_ID);
        return to;
    }

    private TypeOrderResponseDto buildResponseDto(String title, String slug) {
        return new TypeOrderResponseDto(ID, title, slug, BRANCH_ID);
    }

    @Test
    @DisplayName("create - crée un TypeOrder et retourne le DTO")
    void create_success_returnsDto() {
        TypeOrderRequestDto dto = new TypeOrderRequestDto();
        dto.setTitle("Cytologie");
        dto.setSlug("cytologie");

        TypeOrder entity = buildEntity("Cytologie", "cytologie");
        TypeOrderResponseDto responseDto = buildResponseDto("Cytologie", "cytologie");

        when(typeOrderRepository.existsBySlugIgnoreCaseAndBranchId("cytologie", BRANCH_ID)).thenReturn(false);
        when(mapper.toTypeOrderEntity(dto)).thenReturn(entity);
        when(typeOrderRepository.save(any(TypeOrder.class))).thenReturn(entity);
        when(mapper.toTypeOrderResponseDto(entity)).thenReturn(responseDto);

        TypeOrderResponseDto result = typeOrderService.create(dto, BRANCH_ID);

        assertThat(result.slug()).isEqualTo("cytologie");
        verify(typeOrderRepository).save(entity);
    }

    @Test
    @DisplayName("create - slug en doublon → DuplicateResourceException")
    void create_duplicateSlug_throws409() {
        TypeOrderRequestDto dto = new TypeOrderRequestDto();
        dto.setTitle("Cytologie");
        dto.setSlug("cytologie");

        when(typeOrderRepository.existsBySlugIgnoreCaseAndBranchId("cytologie", BRANCH_ID)).thenReturn(true);

        assertThatThrownBy(() -> typeOrderService.create(dto, BRANCH_ID))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("findBySlug - retourne le TypeOrder si trouvé")
    void findBySlug_found_returnsDto() {
        TypeOrder entity = buildEntity("Histologie", "histologie");
        TypeOrderResponseDto responseDto = buildResponseDto("Histologie", "histologie");

        when(typeOrderRepository.findBySlug("histologie")).thenReturn(Optional.of(entity));
        when(mapper.toTypeOrderResponseDto(entity)).thenReturn(responseDto);

        Optional<TypeOrderResponseDto> result = typeOrderService.findBySlug("histologie");

        assertThat(result).isPresent();
        assertThat(result.get().slug()).isEqualTo("histologie");
    }

    @Test
    @DisplayName("findBySlug - retourne empty si slug inconnu")
    void findBySlug_notFound_returnsEmpty() {
        when(typeOrderRepository.findBySlug("inexistant")).thenReturn(Optional.empty());

        Optional<TypeOrderResponseDto> result = typeOrderService.findBySlug("inexistant");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById - lève ResourceNotFoundException si inexistant")
    void findById_notFound_throws404() {
        when(typeOrderRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> typeOrderService.findById(ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete - supprime le TypeOrder")
    void delete_callsRepositoryDelete() {
        TypeOrder entity = buildEntity("Biopsie", "biopsie");

        when(typeOrderRepository.findById(ID)).thenReturn(Optional.of(entity));

        typeOrderService.delete(ID);

        verify(typeOrderRepository).delete(entity);
    }
}
