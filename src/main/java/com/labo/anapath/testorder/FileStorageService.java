package com.labo.anapath.testorder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.storage.path:/tmp/labo/storage}")
    private String storagePath;

    private Path getUploadDir() {
        Path dir = Paths.get(storagePath, "examen_images").toAbsolutePath().normalize();
        try { Files.createDirectories(dir); } catch (IOException e) { throw new RuntimeException(e); }
        return dir;
    }

    public String store(MultipartFile file) throws IOException {
        Path uploadDir = getUploadDir();
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (ext != null ? "." + ext.toLowerCase() : "");
        Path target = uploadDir.resolve(filename).normalize();
        if (!target.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Chemin de fichier invalide");
        }
        file.transferTo(target);
        return "examen_images/" + filename;
    }

    public void delete(String filename) throws IOException {
        Path uploadDir = getUploadDir();
        Path target = uploadDir.resolve(filename).normalize();
        if (!target.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Chemin de fichier invalide");
        }
        Files.deleteIfExists(target);
    }

    public String getUrl(String filename) {
        return "/api/v1/files/examen_images/" + filename;
    }
}
