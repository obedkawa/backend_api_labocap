package com.labo.anapath.inventory;

import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock MovementRepository movementRepository;
    @Mock ArticleRepository articleRepository;
    @Mock InventoryMapper inventoryMapper;

    MovementServiceImpl service;

    private final UUID BRANCH_ID  = UUID.randomUUID();
    private final UUID ARTICLE_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        service = new MovementServiceImpl(movementRepository, articleRepository, inventoryMapper);
    }

    private Article buildArticle(BigDecimal quantity) {
        Article a = new Article();
        ReflectionTestUtils.setField(a, "id", ARTICLE_ID);
        a.setName("Réactif test");
        a.setQuantity(quantity);
        return a;
    }

    private MovementResponseDto dummyDto() {
        return new MovementResponseDto(UUID.randomUUID(), ARTICLE_ID, "Réactif test",
                MovementType.IN, BigDecimal.TEN, null, BRANCH_ID, null);
    }

    @Test
    @DisplayName("create IN → augmente la quantité de l'article")
    void create_IN_shouldIncreaseArticleQuantity() {
        Article article = buildArticle(new BigDecimal("20"));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(article));
        when(movementRepository.save(any())).thenReturn(new Movement());
        when(inventoryMapper.toMovementResponseDto(any())).thenReturn(dummyDto());

        MovementRequestDto dto = new MovementRequestDto();
        dto.setArticleId(ARTICLE_ID);
        dto.setType(MovementType.IN);
        dto.setQuantity(new BigDecimal("10"));

        service.create(dto, BRANCH_ID);

        assertThat(article.getQuantity()).isEqualByComparingTo("30");
        verify(articleRepository).save(article);
    }

    @Test
    @DisplayName("create OUT stock suffisant → diminue la quantité de l'article")
    void create_OUT_sufficientStock_shouldDecreaseQuantity() {
        Article article = buildArticle(new BigDecimal("50"));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(article));
        when(movementRepository.save(any())).thenReturn(new Movement());
        when(inventoryMapper.toMovementResponseDto(any())).thenReturn(dummyDto());

        MovementRequestDto dto = new MovementRequestDto();
        dto.setArticleId(ARTICLE_ID);
        dto.setType(MovementType.OUT);
        dto.setQuantity(new BigDecimal("15"));

        service.create(dto, BRANCH_ID);

        assertThat(article.getQuantity()).isEqualByComparingTo("35");
    }

    @Test
    @DisplayName("create OUT stock insuffisant → BusinessException")
    void create_OUT_insufficientStock_shouldThrowBusinessException() {
        Article article = buildArticle(new BigDecimal("5"));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(article));

        MovementRequestDto dto = new MovementRequestDto();
        dto.setArticleId(ARTICLE_ID);
        dto.setType(MovementType.OUT);
        dto.setQuantity(new BigDecimal("10"));

        assertThatThrownBy(() -> service.create(dto, BRANCH_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock insuffisant");
    }

    @Test
    @DisplayName("create ADJUSTMENT → remplace directement la quantité")
    void create_ADJUSTMENT_shouldReplaceQuantityDirectly() {
        Article article = buildArticle(new BigDecimal("100"));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(article));
        when(movementRepository.save(any())).thenReturn(new Movement());
        when(inventoryMapper.toMovementResponseDto(any())).thenReturn(dummyDto());

        MovementRequestDto dto = new MovementRequestDto();
        dto.setArticleId(ARTICLE_ID);
        dto.setType(MovementType.ADJUSTMENT);
        dto.setQuantity(new BigDecimal("42"));

        service.create(dto, BRANCH_ID);

        assertThat(article.getQuantity()).isEqualByComparingTo("42");
    }

    @Test
    @DisplayName("create - article inconnu → ResourceNotFoundException")
    void create_unknownArticle_shouldThrow404() {
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        MovementRequestDto dto = new MovementRequestDto();
        dto.setArticleId(ARTICLE_ID);
        dto.setType(MovementType.IN);
        dto.setQuantity(BigDecimal.ONE);

        assertThatThrownBy(() -> service.create(dto, BRANCH_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findAll - retourne page triée par branche")
    void findAll_shouldReturnPagedResults() {
        Page<Movement> page = new PageImpl<>(List.of(new Movement()));
        when(movementRepository.findByBranchId(any(UUID.class), any(Pageable.class))).thenReturn(page);
        when(inventoryMapper.toMovementResponseDto(any())).thenReturn(dummyDto());

        var result = service.findAll(0, 20, BRANCH_ID);

        assertThat(result).isNotNull();
    }
}
