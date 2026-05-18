package com.labo.anapath.categoryprestation;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryPrestationServiceImpl implements CategoryPrestationService {

    private final CategoryPrestationRepository repository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryPrestationResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(repository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("name")))
                .map(this::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryPrestationResponseDto findById(UUID id) {
        return toDto(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie prestation", id)));
    }

    @Override
    @Transactional
    public CategoryPrestationResponseDto create(CategoryPrestationRequestDto dto, UUID branchId) {
        CategoryPrestation cat = new CategoryPrestation();
        cat.setBranchId(branchId);
        cat.setName(dto.getName());
        cat.setSlug(toSlug(dto.getName()));
        return toDto(repository.save(cat));
    }

    @Override
    @Transactional
    public CategoryPrestationResponseDto update(UUID id, CategoryPrestationRequestDto dto) {
        CategoryPrestation cat = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie prestation", id));
        cat.setName(dto.getName());
        cat.setSlug(toSlug(dto.getName()));
        return toDto(repository.save(cat));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CategoryPrestation cat = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie prestation", id));
        if (!cat.getPrestations().isEmpty()) {
            throw new InvalidOperationException(
                    "Impossible de supprimer : cette catégorie contient des prestations");
        }
        repository.delete(cat);
    }

    private CategoryPrestationResponseDto toDto(CategoryPrestation cat) {
        return new CategoryPrestationResponseDto(
                cat.getId(), cat.getName(), cat.getSlug(), cat.getBranchId(), cat.getCreatedAt());
    }

    private String toSlug(String name) {
        return name.toLowerCase().trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
