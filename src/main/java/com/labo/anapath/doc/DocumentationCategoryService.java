package com.labo.anapath.doc;

import java.util.List;
import java.util.UUID;

public interface DocumentationCategoryService {
    List<DocumentationCategoryResponseDto> findAll(UUID branchId);
    DocumentationCategoryResponseDto findById(UUID id);
    DocumentationCategoryResponseDto create(DocumentationCategoryRequestDto dto, UUID branchId);
    DocumentationCategoryResponseDto update(UUID id, DocumentationCategoryRequestDto dto);
    void delete(UUID id);
}
