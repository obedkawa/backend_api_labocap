package com.labo.anapath.doc;

import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentationCategoryServiceImpl implements DocumentationCategoryService {

    private final DocumentationCategoryRepository documentationCategoryRepository;
    private final DocumentationCategoryMapper documentationCategoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DocumentationCategoryResponseDto> findAll(UUID branchId) {
        return documentationCategoryRepository.findByBranchId(branchId)
                .stream().map(documentationCategoryMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentationCategoryResponseDto findById(UUID id) {
        return documentationCategoryMapper.toResponseDto(documentationCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie de documentation", id)));
    }

    @Override
    @Transactional
    public DocumentationCategoryResponseDto create(DocumentationCategoryRequestDto dto, UUID branchId) {
        DocumentationCategory cat = new DocumentationCategory();
        cat.setBranchId(branchId);
        cat.setName(dto.getName());
        return documentationCategoryMapper.toResponseDto(documentationCategoryRepository.save(cat));
    }

    @Override
    @Transactional
    public DocumentationCategoryResponseDto update(UUID id, DocumentationCategoryRequestDto dto) {
        DocumentationCategory cat = documentationCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie de documentation", id));
        cat.setName(dto.getName());
        return documentationCategoryMapper.toResponseDto(documentationCategoryRepository.save(cat));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        DocumentationCategory cat = documentationCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie de documentation", id));
        documentationCategoryRepository.delete(cat);
    }
}
