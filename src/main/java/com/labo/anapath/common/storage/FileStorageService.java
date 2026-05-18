package com.labo.anapath.common.storage;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {

    String store(MultipartFile file, String directory);

    Path resolve(String relativePath);

    void delete(String relativePath);
}
