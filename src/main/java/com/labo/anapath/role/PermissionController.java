package com.labo.anapath.role;

import com.labo.anapath.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contrôleur REST exposant la liste des permissions disponibles dans le système.
 *
 * <p>Les permissions sont en lecture seule via l'API : leur création et leur
 * modification sont gérées par les scripts de seeding de base de données.
 * Tout utilisateur authentifié peut consulter la liste pour alimenter
 * les interfaces de gestion des rôles.</p>
 *
 * <p>Base URL : {@code /api/v1/permissions}</p>
 */
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    /**
     * Retourne la liste complète de toutes les permissions du système.
     * Accessible à tout utilisateur authentifié (pour alimenter les écrans
     * d'assignation de permissions aux rôles).
     *
     * @return liste de {@link PermissionResponseDto}
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PermissionResponseDto>>> findAll() {
        List<PermissionResponseDto> result = permissionRepository.findAll()
                .stream().map(permissionMapper::toResponseDto).toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
