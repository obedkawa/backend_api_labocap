package com.labo.anapath.test;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implémentation de {@link UnitMeasurementService} gérant la logique métier des unités de mesure.
 *
 * <p>Responsabilités principales :
 * <ul>
 *   <li>Protection contre la suppression d'une unité référencée par des analyses</li>
 *   <li>Mise à jour directe des champs nom et abréviation</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UnitMeasurementServiceImpl implements UnitMeasurementService {

    private final UnitMeasurementRepository unitMeasurementRepository;
    /** Utilisé pour vérifier les dépendances avant suppression d'une unité. */
    private final LabTestRepository labTestRepository;
    private final TestCatalogueMapper mapper;

    /**
     * {@inheritDoc}
     * Les résultats sont triés par date de création décroissante.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<UnitMeasurementResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(
                unitMeasurementRepository.findByBranchId(branchId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
                        .map(mapper::toUnitMeasurementResponseDto));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UnitMeasurementResponseDto findById(UUID id) {
        return mapper.toUnitMeasurementResponseDto(
                unitMeasurementRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Unité", id)));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UnitMeasurementResponseDto create(UnitMeasurementRequestDto dto, UUID branchId) {
        UnitMeasurement entity = mapper.toUnitMeasurementEntity(dto);
        entity.setBranchId(branchId);
        UnitMeasurement saved = unitMeasurementRepository.save(entity);
        log.info("Unité créée: {}", saved.getId());
        return mapper.toUnitMeasurementResponseDto(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Les deux champs éditables ({@code name}, {@code abbreviation})
     * sont remplacés intégralement depuis le DTO.</p>
     */
    @Override
    @Transactional
    public UnitMeasurementResponseDto update(UUID id, UnitMeasurementRequestDto dto) {
        UnitMeasurement entity = unitMeasurementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unité", id));
        entity.setName(dto.getName());
        entity.setAbbreviation(dto.getAbbreviation());
        return mapper.toUnitMeasurementResponseDto(unitMeasurementRepository.save(entity));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Avant suppression, vérifie qu'aucune analyse ({@link LabTest}) n'utilise
     * cette unité, afin de préserver l'intégrité référentielle du catalogue.</p>
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        UnitMeasurement entity = unitMeasurementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unité", id));
        // Empêcher la suppression si des analyses référencent cette unité
        if (labTestRepository.existsByUnitMeasurement(entity)) {
            throw new BusinessException("Cette unité est référencée par des analyses.");
        }
        unitMeasurementRepository.delete(entity);
    }
}
