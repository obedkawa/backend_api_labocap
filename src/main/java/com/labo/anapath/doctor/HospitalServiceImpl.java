package com.labo.anapath.doctor;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.DuplicateResourceException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implémentation de {@link HospitalService} portant la logique métier des hôpitaux.
 * <p>
 * Les règles métier appliquées sont :
 * <ul>
 *   <li>Unicité du nom au sein d'une agence lors de la création</li>
 *   <li>Unicité du nom lors de la mise à jour (en excluant l'hôpital modifié)</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalMapper hospitalMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<HospitalResponseDto> findAll(int page, int size, UUID branchId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<HospitalResponseDto> result = hospitalRepository.findByBranchId(branchId, pageRequest)
                .map(hospitalMapper::toResponseDto);
        return PageResponse.of(result);
    }

    @Override
    @Transactional(readOnly = true)
    public HospitalResponseDto findById(UUID id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hôpital", id));
        return hospitalMapper.toResponseDto(hospital);
    }

    @Override
    @Transactional
    public HospitalResponseDto create(HospitalRequestDto dto, UUID branchId) {
        if (hospitalRepository.existsByNameIgnoreCaseAndBranchId(dto.getName(), branchId)) {
            throw new DuplicateResourceException("Un hôpital avec le nom '" + dto.getName() + "' existe déjà.");
        }
        Hospital hospital = hospitalMapper.toEntity(dto);
        hospital.setBranchId(branchId);
        Hospital saved = hospitalRepository.save(hospital);
        log.info("Hôpital créé: {}", saved.getId());
        return hospitalMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public HospitalResponseDto update(UUID id, HospitalRequestDto dto) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hôpital", id));
        // Vérifier le doublon de nom uniquement si un nouveau nom est fourni
        if (dto.getName() != null && hospitalRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot(dto.getName(), hospital.getBranchId(), id)) {
            throw new DuplicateResourceException("Un hôpital avec le nom '" + dto.getName() + "' existe déjà.");
        }
        hospitalMapper.updateEntityFromDto(dto, hospital);
        return hospitalMapper.toResponseDto(hospitalRepository.save(hospital));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hôpital", id));
        hospitalRepository.delete(hospital);
        log.info("Hôpital supprimé (soft): {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HospitalResponseDto> search(String q, UUID branchId) {
        return hospitalRepository.findByNameContainingIgnoreCaseAndBranchId(q, branchId)
                .stream()
                .map(hospitalMapper::toResponseDto)
                .toList();
    }
}
