package com.labo.anapath.role;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur REST gérant les opérations CRUD sur les rôles et l'assignation de permissions.
 *
 * <p>Toutes les routes sont protégées par la permission {@code manage-roles}.
 * Les rôles sont scopés par succursale via le {@code branchId} du principal connecté.</p>
 *
 * <p>Base URL : {@code /api/v1/roles}</p>
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * Retourne la liste paginée des rôles de la succursale courante.
     *
     * @param page      numéro de page (0-indexé, défaut 0)
     * @param size      nombre d'éléments par page (défaut 20)
     * @param principal principal de sécurité de l'utilisateur connecté
     * @return page de {@link RoleResponseDto}
     */
    @GetMapping
    @PreAuthorize("hasAuthority('manage-roles')")
    public ResponseEntity<ApiResponse<PageResponse<RoleResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(roleService.findAll(page, size, principal.getBranchId())));
    }

    /**
     * Retourne un rôle par son identifiant unique.
     *
     * @param id identifiant UUID du rôle
     * @return le DTO du rôle trouvé
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-roles')")
    public ResponseEntity<ApiResponse<RoleResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.findById(id)));
    }

    /**
     * Crée un nouveau rôle dans la succursale de l'utilisateur connecté.
     * Le slug est généré automatiquement à partir du nom.
     *
     * @param dto       données de création validées
     * @param principal principal de sécurité fournissant le branchId
     * @return le DTO du rôle créé avec le statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('manage-roles')")
    public ResponseEntity<ApiResponse<RoleResponseDto>> create(
            @Valid @RequestBody RoleRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        RoleResponseDto created = roleService.create(dto, principal.getBranchId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Rôle créé avec succès", created));
    }

    /**
     * Met à jour les informations d'un rôle existant.
     *
     * @param id  identifiant UUID du rôle à modifier
     * @param dto nouvelles données validées
     * @return le DTO mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-roles')")
    public ResponseEntity<ApiResponse<RoleResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody RoleRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Rôle mis à jour", roleService.update(id, dto)));
    }

    /**
     * Supprime (soft-delete) un rôle par son identifiant.
     *
     * @param id identifiant UUID du rôle à supprimer
     * @return réponse vide avec message de confirmation
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-roles')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Rôle supprimé", null));
    }

    /**
     * Remplace intégralement la liste des permissions d'un rôle.
     *
     * @param id            identifiant UUID du rôle
     * @param permissionIds liste des identifiants de permissions à assigner
     * @return le DTO du rôle mis à jour avec ses nouvelles permissions
     */
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('manage-roles')")
    public ResponseEntity<ApiResponse<RoleResponseDto>> assignPermissions(
            @PathVariable UUID id,
            @RequestBody List<UUID> permissionIds) {
        return ResponseEntity.ok(ApiResponse.success("Permissions assignées", roleService.assignPermissions(id, permissionIds)));
    }
}
