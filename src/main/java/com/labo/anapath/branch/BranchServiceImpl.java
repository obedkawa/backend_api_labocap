package com.labo.anapath.branch;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.DuplicateResourceException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implémentation de {@link BranchService} portant la logique métier des agences.
 * <p>
 * Toutes les opérations d'écriture sont transactionnelles. Les lectures utilisent
 * {@code readOnly = true} pour optimiser les performances et éviter les flush JPA inutiles.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    // Utilisé pour vérifier qu'aucun utilisateur ne dépend de l'agence avant suppression
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BranchResponseDto> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BranchResponseDto> result = branchRepository.findAll(pageRequest)
                .map(branchMapper::toResponseDto);
        return PageResponse.of(result);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponseDto findById(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
        return branchMapper.toResponseDto(branch);
    }

    @Override
    @Transactional
    public BranchResponseDto create(BranchRequestDto dto) {
        if (branchRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Une agence avec le nom '" + dto.getName() + "' existe déjà.");
        }
        Branch branch = branchMapper.toEntity(dto);
        Branch saved = branchRepository.save(branch);
        log.info("Agence créée: {}", saved.getId());
        return branchMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public BranchResponseDto update(UUID id, BranchRequestDto dto) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
        if (dto.getName() != null && branchRepository.existsByNameIgnoreCaseAndIdNot(dto.getName(), id)) {
            throw new DuplicateResourceException("Une agence avec le nom '" + dto.getName() + "' existe déjà.");
        }
        branchMapper.updateEntityFromDto(dto, branch);
        return branchMapper.toResponseDto(branchRepository.save(branch));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        // Interdire la suppression si des utilisateurs sont rattachés à cette agence
        if (userRepository.existsByBranchId(id)) {
            throw new BusinessException("La branche a des utilisateurs liés.");
        }
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
        branchRepository.delete(branch);
        log.info("Agence supprimée (soft): {}", id);
    }
}
