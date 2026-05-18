package com.labo.anapath.test;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implémentation de {@link DataCodeService} gérant la logique métier des codes de référence.
 *
 * <p>Les mises à jour sont effectuées par affectation directe des champs
 * (pas de mapper partiel) car tous les champs sont remplacés intégralement.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataCodeServiceImpl implements DataCodeService {

    private final DataCodeRepository dataCodeRepository;
    private final TestCatalogueMapper mapper;

    /**
     * {@inheritDoc}
     * Les résultats sont triés par date de création décroissante.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<DataCodeResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(
                dataCodeRepository.findByBranchId(branchId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
                        .map(mapper::toDataCodeResponseDto));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public DataCodeResponseDto findById(UUID id) {
        return mapper.toDataCodeResponseDto(
                dataCodeRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("DataCode", id)));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public DataCodeResponseDto create(DataCodeRequestDto dto, UUID branchId) {
        DataCode entity = mapper.toDataCodeEntity(dto);
        entity.setBranchId(branchId);
        DataCode saved = dataCodeRepository.save(entity);
        log.info("DataCode créé: {}", saved.getId());
        return mapper.toDataCodeResponseDto(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Les trois champs éditables ({@code code}, {@code label}, {@code type})
     * sont remplacés intégralement depuis le DTO.</p>
     */
    @Override
    @Transactional
    public DataCodeResponseDto update(UUID id, DataCodeRequestDto dto) {
        DataCode entity = dataCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DataCode", id));
        entity.setCode(dto.getCode());
        entity.setLabel(dto.getLabel());
        entity.setType(dto.getType());
        return mapper.toDataCodeResponseDto(dataCodeRepository.save(entity));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(UUID id) {
        DataCode entity = dataCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DataCode", id));
        dataCodeRepository.delete(entity);
    }
}
