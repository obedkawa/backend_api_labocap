package com.labo.anapath.doc;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.common.storage.FileStorageService;
import com.labo.anapath.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocServiceImpl implements DocService {

    private final DocRepository docRepository;
    private final DocVersionRepository docVersionRepository;
    private final DocumentationCategoryRepository documentationCategoryRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final DocMapper docMapper;
    private final DocVersionMapper docVersionMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(docRepository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(docMapper::toResponseDto));
    }

    @Override
    @Transactional(readOnly = true)
    public DocResponseDto findById(UUID id) {
        return docMapper.toResponseDto(docRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id)));
    }

    @Override
    @Transactional
    public DocResponseDto create(String title, UUID documentationCategoryId, MultipartFile file, UUID userId, UUID branchId) {
        String path = fileStorageService.store(file, "documents");
        long size = file.getSize();

        Doc doc = new Doc();
        doc.setBranchId(branchId);
        doc.setTitle(title);
        doc.setAttachment(path);
        doc.setFileSize(size);
        doc.setIsCurrentVersion(true);
        doc.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId)));
        if (documentationCategoryId != null) {
            doc.setDocumentationCategory(documentationCategoryRepository.findById(documentationCategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie", documentationCategoryId)));
        }
        doc = docRepository.save(doc);

        DocVersion version = new DocVersion();
        version.setBranchId(branchId);
        version.setDoc(doc);
        version.setTitle(title);
        version.setAttachment(path);
        version.setFileSize(size);
        version.setVersion(1);
        version.setUser(doc.getUser());
        docVersionRepository.save(version);

        return docMapper.toResponseDto(doc);
    }

    @Override
    @Transactional
    public DocVersionResponseDto addVersion(UUID docId, String title, MultipartFile file, UUID userId, UUID branchId) {
        Doc doc = docRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", docId));
        int nextVersion = docVersionRepository.findTopByDocIdOrderByVersionDesc(docId)
                .map(v -> v.getVersion() + 1)
                .orElse(2);
        String path = fileStorageService.store(file, "documents");

        DocVersion version = new DocVersion();
        version.setBranchId(branchId);
        version.setDoc(doc);
        version.setTitle(title != null ? title : doc.getTitle());
        version.setAttachment(path);
        version.setFileSize(file.getSize());
        version.setVersion(nextVersion);
        version.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId)));
        return docVersionMapper.toResponseDto(docVersionRepository.save(version));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocVersionResponseDto> getVersions(UUID docId) {
        return docVersionRepository.findByDocIdOrderByVersionAsc(docId)
                .stream().map(docVersionMapper::toResponseDto).toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Doc doc = docRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        docRepository.delete(doc);
    }
}
