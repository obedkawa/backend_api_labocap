package com.labo.anapath.doc;

import com.labo.anapath.common.dto.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocService {
    PageResponse<DocResponseDto> findAll(int page, int size, UUID branchId);
    DocResponseDto findById(UUID id);
    DocResponseDto create(String title, UUID documentationCategoryId, MultipartFile file, UUID userId, UUID branchId);
    DocVersionResponseDto addVersion(UUID docId, String title, MultipartFile file, UUID userId, UUID branchId);
    List<DocVersionResponseDto> getVersions(UUID docId);
    void delete(UUID id);
}
