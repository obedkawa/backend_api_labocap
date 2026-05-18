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
 * Implémentation de {@link DoctorService} portant la logique métier des médecins prescripteurs.
 * <p>
 * Les règles métier appliquées sont :
 * <ul>
 *   <li>Unicité du nom au sein d'une agence lors de la création</li>
 *   <li>Unicité du nom au sein de l'agence du médecin lors de la mise à jour
 *       (le branchId est récupéré depuis l'entité stockée, pas du contexte de sécurité)</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DoctorResponseDto> findAll(int page, int size, UUID branchId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DoctorResponseDto> result = doctorRepository.findByBranchId(branchId, pageRequest)
                .map(doctorMapper::toResponseDto);
        return PageResponse.of(result);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponseDto findById(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", id));
        return doctorMapper.toResponseDto(doctor);
    }

    @Override
    @Transactional
    public DoctorResponseDto create(DoctorRequestDto dto, UUID branchId) {
        if (doctorRepository.existsByNameIgnoreCaseAndBranchId(dto.getName(), branchId)) {
            throw new DuplicateResourceException("Un médecin '" + dto.getName() + "' existe déjà.");
        }
        Doctor doctor = doctorMapper.toEntity(dto);
        doctor.setBranchId(branchId);
        Doctor saved = doctorRepository.save(doctor);
        log.info("Médecin créé: {}", saved.getId());
        return doctorMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public DoctorResponseDto update(UUID id, DoctorRequestDto dto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", id));
        // Vérifier le doublon de nom uniquement si un nouveau nom est fourni
        if (dto.getName() != null &&
                doctorRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot(
                        dto.getName(), doctor.getBranchId(), id)) {
            throw new DuplicateResourceException("Un médecin '" + dto.getName() + "' existe déjà.");
        }
        doctorMapper.updateEntityFromDto(dto, doctor);
        return doctorMapper.toResponseDto(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", id));
        doctorRepository.delete(doctor);
        log.info("Médecin supprimé (soft): {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponseDto> search(String q, UUID branchId) {
        return doctorRepository.searchByNameAndBranchId(q, branchId)
                .stream()
                .map(doctorMapper::toResponseDto)
                .toList();
    }
}
