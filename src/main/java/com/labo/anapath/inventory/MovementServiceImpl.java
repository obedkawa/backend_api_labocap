package com.labo.anapath.inventory;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implémentation de {@link MovementService} gérant la logique métier
 * des mouvements de stock et la mise à jour des quantités des articles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MovementServiceImpl implements MovementService {

    private final MovementRepository movementRepository;
    private final ArticleRepository articleRepository;
    private final InventoryMapper inventoryMapper;

    /**
     * {@inheritDoc}
     * Les mouvements sont triés par date de création décroissante.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<MovementResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(movementRepository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(inventoryMapper::toMovementResponseDto));
    }

    /**
     * {@inheritDoc}
     * La mise à jour du stock suit les règles suivantes :
     * <ul>
     *   <li>IN : la quantité est ajoutée au stock</li>
     *   <li>OUT : la quantité est soustraite ; une exception est levée si le stock devient négatif</li>
     *   <li>ADJUSTMENT : le stock est remplacé par la quantité fournie (inventaire physique)</li>
     * </ul>
     */
    @Override
    @Transactional
    public MovementResponseDto create(MovementRequestDto dto, UUID branchId) {
        Article article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", dto.getArticleId()));

        Movement movement = new Movement();
        movement.setBranchId(branchId);
        movement.setArticle(article);
        movement.setType(dto.getType());
        movement.setQuantity(dto.getQuantity());
        movement.setNotes(dto.getNotes());

        // Mise à jour du stock selon le type de mouvement
        if (dto.getType() == MovementType.IN) {
            article.setQuantity(article.getQuantity().add(dto.getQuantity()));
        } else if (dto.getType() == MovementType.OUT) {
            BigDecimal newQty = article.getQuantity().subtract(dto.getQuantity());
            if (newQty.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("Stock insuffisant pour l'article: " + article.getName()
                        + ". Stock actuel: " + article.getQuantity());
            }
            article.setQuantity(newQty);
        } else if (dto.getType() == MovementType.ADJUSTMENT) {
            // Ajustement : remplacement direct de la quantité (inventaire physique)
            article.setQuantity(dto.getQuantity());
        }

        articleRepository.save(article);
        Movement saved = movementRepository.save(movement);
        log.info("Mouvement créé: {} - {} - {}", dto.getType(), article.getName(), dto.getQuantity());
        return inventoryMapper.toMovementResponseDto(saved);
    }
}
