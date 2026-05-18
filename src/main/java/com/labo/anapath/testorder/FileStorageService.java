package com.labo.anapath.testorder;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads/examen_images");

    public String store(MultipartFile file) throws IOException {
        Files.createDirectories(uploadDir);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        file.transferTo(uploadDir.resolve(filename));
        return filename;
    }

    public void delete(String filename) throws IOException {
        Files.deleteIfExists(uploadDir.resolve(filename));
    }

    public String getUrl(String filename) {
        return "/uploads/examen_images/" + filename;
    }
}
