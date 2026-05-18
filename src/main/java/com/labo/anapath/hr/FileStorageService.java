package com.labo.anapath.hr;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String store(MultipartFile file, String subDirectory);
    Resource load(String filePath);
    void delete(String filePath);
}
