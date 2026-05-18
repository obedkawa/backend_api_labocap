package com.labo.anapath.support;

import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProblemCategoryServiceImpl implements ProblemCategoryService {

    private final ProblemCategoryRepository problemCategoryRepository;
    private final ProblemCategoryMapper problemCategoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProblemCategoryResponseDto> findAll(UUID branchId) {
        return problemCategoryRepository.findByBranchId(branchId)
                .stream().map(problemCategoryMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemCategoryResponseDto findById(UUID id) {
        return problemCategoryMapper.toResponseDto(problemCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie de problème", id)));
    }

    @Override
    @Transactional
    public ProblemCategoryResponseDto create(ProblemCategoryRequestDto dto, UUID branchId) {
        if (problemCategoryRepository.findByNameAndBranchId(dto.getName(), branchId).isPresent()) {
            throw new InvalidOperationException("Une catégorie avec ce nom existe déjà");
        }
        ProblemCategory category = new ProblemCategory();
        category.setBranchId(branchId);
        category.setName(dto.getName());
        return problemCategoryMapper.toResponseDto(problemCategoryRepository.save(category));
    }

    @Override
    @Transactional
    public ProblemCategoryResponseDto update(UUID id, ProblemCategoryRequestDto dto) {
        ProblemCategory category = problemCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie de problème", id));
        category.setName(dto.getName());
        return problemCategoryMapper.toResponseDto(problemCategoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ProblemCategory category = problemCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie de problème", id));
        problemCategoryRepository.delete(category);
    }
}
