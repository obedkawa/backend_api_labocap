package com.labo.anapath.prestation;

import com.labo.anapath.categoryprestation.CategoryPrestation;
import com.labo.anapath.categoryprestation.CategoryPrestationRepository;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.prestationorder.PrestationOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrestationServiceImpl implements PrestationService {

    private final PrestationRepository repository;
    private final CategoryPrestationRepository categoryRepository;
    private final PrestationOrderRepository prestationOrderRepository;
    private final PrestationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PrestationResponseDto> findAll(int page, int size, UUID branchId, UUID categoryId) {
        var pageable = PageRequest.of(page, size, Sort.by("name"));
        if (categoryId != null) {
            return PageResponse.of(repository.findByCategoryPrestationIdAndBranchId(categoryId, branchId, pageable)
                    .map(mapper::toResponseDto));
        }
        return PageResponse.of(repository.findByBranchId(branchId, pageable).map(mapper::toResponseDto));
    }

    @Override
    @Transactional(readOnly = true)
    public PrestationResponseDto findById(UUID id) {
        return mapper.toResponseDto(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestation", id)));
    }

    @Override
    @Transactional
    public PrestationResponseDto create(PrestationRequestDto dto, UUID branchId) {
        CategoryPrestation category = categoryRepository.findById(dto.getCategoryPrestationId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie prestation", dto.getCategoryPrestationId()));
        Prestation prestation = new Prestation();
        prestation.setBranchId(branchId);
        prestation.setName(dto.getName());
        prestation.setPrice(dto.getPrice());
        prestation.setDescription(dto.getDescription());
        prestation.setCategoryPrestation(category);
        return mapper.toResponseDto(repository.save(prestation));
    }

    @Override
    @Transactional
    public PrestationResponseDto update(UUID id, PrestationRequestDto dto) {
        Prestation prestation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestation", id));
        CategoryPrestation category = categoryRepository.findById(dto.getCategoryPrestationId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie prestation", dto.getCategoryPrestationId()));
        prestation.setName(dto.getName());
        prestation.setPrice(dto.getPrice());
        prestation.setDescription(dto.getDescription());
        prestation.setCategoryPrestation(category);
        return mapper.toResponseDto(repository.save(prestation));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestation", id));
        if (prestationOrderRepository.existsByPrestationId(id)) {
            throw new InvalidOperationException(
                    "Impossible de supprimer : cette prestation est utilisée par des orders");
        }
        repository.deleteById(id);
    }
}
