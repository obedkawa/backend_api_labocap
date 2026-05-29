package com.labo.anapath.hr;

import com.labo.anapath.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service("hrFileStorageServiceImpl")
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.storage.path:/tmp/labo/storage}")
    private String basePath;

    @Override
    public String store(MultipartFile file, String subDirectory) {
        try {
            String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            String safeFilename = UUID.randomUUID() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path dir = Paths.get(basePath, subDirectory);
            Files.createDirectories(dir);
            Path target = dir.resolve(safeFilename);
            file.transferTo(target);
            return subDirectory + "/" + safeFilename;
        } catch (IOException e) {
            throw new BusinessException("Erreur lors du stockage du fichier: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filePath) {
        try {
            Path base = Paths.get(basePath).toAbsolutePath().normalize();
            Path resolved = base.resolve(filePath).normalize();
            if (!resolved.startsWith(base)) {
                throw new BusinessException("Chemin de fichier invalide: " + filePath);
            }
            return new UrlResource(resolved.toUri());
        } catch (MalformedURLException e) {
            throw new BusinessException("Chemin de fichier invalide: " + filePath);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            Path base = Paths.get(basePath).toAbsolutePath().normalize();
            Path resolved = base.resolve(filePath).normalize();
            if (!resolved.startsWith(base)) {
                throw new BusinessException("Chemin de fichier invalide: " + filePath);
            }
            Files.deleteIfExists(resolved);
        } catch (IOException e) {
            log.warn("Impossible de supprimer le fichier physique: {}", filePath);
        }
    }
}
