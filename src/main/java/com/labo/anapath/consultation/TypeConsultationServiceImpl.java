package com.labo.anapath.consultation;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TypeConsultationServiceImpl implements TypeConsultationService {

    private final TypeConsultationRepository typeConsultationRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TypeConsultationResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(typeConsultationRepository.findByBranchId(branchId,
                PageRequest.of(page, size)).map(this::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public TypeConsultationResponseDto findById(UUID id) {
        return toDto(typeConsultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type consultation", id)));
    }

    @Override
    @Transactional
    public TypeConsultationResponseDto create(TypeConsultationRequestDto dto, UUID branchId) {
        TypeConsultation tc = new TypeConsultation();
        tc.setBranchId(branchId);
        tc.setName(dto.getName());
        tc.setSlug(toSlug(dto.getName()));
        return toDto(typeConsultationRepository.save(tc));
    }

    @Override
    @Transactional
    public TypeConsultationResponseDto update(UUID id, TypeConsultationRequestDto dto) {
        TypeConsultation tc = typeConsultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type consultation", id));
        tc.setName(dto.getName());
        tc.setSlug(toSlug(dto.getName()));
        return toDto(typeConsultationRepository.save(tc));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        typeConsultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type consultation", id));
        typeConsultationRepository.deleteById(id);
    }

    private TypeConsultationResponseDto toDto(TypeConsultation tc) {
        return new TypeConsultationResponseDto(tc.getId(), tc.getName(), tc.getBranchId(), tc.getCreatedAt());
    }

    private String toSlug(String name) {
        return name.toLowerCase().trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
