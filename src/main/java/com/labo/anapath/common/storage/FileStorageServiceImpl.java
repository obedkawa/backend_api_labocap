package com.labo.anapath.common.storage;

import com.labo.anapath.common.exception.InvalidOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path basePath;

    public FileStorageServiceImpl(@Value("${app.storage.path:./storage}") String basePath) {
        this.basePath = Paths.get(basePath).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, String directory) {
        try {
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + (extension != null ? "." + extension : "");
            Path targetDir = basePath.resolve(directory);
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return directory + "/" + filename;
        } catch (IOException e) {
            throw new InvalidOperationException("Erreur lors du stockage du fichier: " + e.getMessage());
        }
    }

    @Override
    public Path resolve(String relativePath) {
        return basePath.resolve(relativePath).normalize();
    }

    @Override
    public void delete(String relativePath) {
        try {
            Files.deleteIfExists(resolve(relativePath));
        } catch (IOException e) {
            log.warn("Impossible de supprimer le fichier {}: {}", relativePath, e.getMessage());
        }
    }
}
