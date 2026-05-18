package com.labo.anapath.test;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.DuplicateResourceException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation de {@link TypeOrderService} gérant la logique métier des types de bons.
 *
 * <p>Responsabilités principales :
 * <ul>
 *   <li>Vérification de l'unicité du slug (insensible à la casse) dans la succursale</li>
 *   <li>Mise à jour directe des champs titre et slug</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TypeOrderServiceImpl implements TypeOrderService {

    private final TypeOrderRepository typeOrderRepository;
    private final TestCatalogueMapper mapper;

    /**
     * {@inheritDoc}
     * Les résultats sont triés par date de création décroissante.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<TypeOrderResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(
                typeOrderRepository.findByBranchId(branchId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
                        .map(mapper::toTypeOrderResponseDto));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public TypeOrderResponseDto findById(UUID id) {
        return mapper.toTypeOrderResponseDto(
                typeOrderRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Type de bon", id)));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public Optional<TypeOrderResponseDto> findBySlug(String slug) {
        return typeOrderRepository.findBySlug(slug).map(mapper::toTypeOrderResponseDto);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public TypeOrderResponseDto create(TypeOrderRequestDto dto, UUID branchId) {
        if (typeOrderRepository.existsBySlugIgnoreCaseAndBranchId(dto.getSlug(), branchId)) {
            throw new DuplicateResourceException("Un type '" + dto.getSlug() + "' existe déjà.");
        }
        TypeOrder entity = mapper.toTypeOrderEntity(dto);
        entity.setBranchId(branchId);
        TypeOrder saved = typeOrderRepository.save(entity);
        log.info("TypeOrder créé: {}", saved.getId());
        return mapper.toTypeOrderResponseDto(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>L'unicité du nouveau slug est vérifiée en excluant l'entité courante
     * afin de permettre la mise à jour sans faux conflit.</p>
     */
    @Override
    @Transactional
    public TypeOrderResponseDto update(UUID id, TypeOrderRequestDto dto) {
        TypeOrder entity = typeOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de bon", id));
        if (typeOrderRepository.existsBySlugIgnoreCaseAndBranchIdAndIdNot(dto.getSlug(), entity.getBranchId(), id)) {
            throw new DuplicateResourceException("Un type '" + dto.getSlug() + "' existe déjà.");
        }
        entity.setTitle(dto.getTitle());
        entity.setSlug(dto.getSlug());
        return mapper.toTypeOrderResponseDto(typeOrderRepository.save(entity));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(UUID id) {
        TypeOrder entity = typeOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de bon", id));
        typeOrderRepository.delete(entity);
    }
}
