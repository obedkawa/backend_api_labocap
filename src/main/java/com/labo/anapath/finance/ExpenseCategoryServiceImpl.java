package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExpenseCategoryResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(categoryRepository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("name").ascending()))
                .map(this::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseCategoryResponseDto findById(UUID id) {
        return toDto(categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseCategory", id)));
    }

    @Override
    @Transactional
    public ExpenseCategoryResponseDto create(ExpenseCategoryRequestDto dto, UUID branchId) {
        ExpenseCategory category = new ExpenseCategory();
        category.setBranchId(branchId);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public ExpenseCategoryResponseDto update(UUID id, ExpenseCategoryRequestDto dto) {
        ExpenseCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseCategory", id));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ExpenseCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseCategory", id));
        categoryRepository.delete(category);
    }

    private ExpenseCategoryResponseDto toDto(ExpenseCategory c) {
        return new ExpenseCategoryResponseDto(c.getId(), c.getName(), c.getDescription(),
                c.getBranchId(), c.getCreatedAt());
    }
}
