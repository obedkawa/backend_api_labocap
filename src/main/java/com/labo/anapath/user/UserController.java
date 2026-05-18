package com.labo.anapath.user;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Contrôleur REST gérant les opérations CRUD sur les utilisateurs.
 *
 * <p>Toutes les routes sont protégées par la permission {@code manage-users}.
 * Les données sont filtrées par la succursale de l'utilisateur connecté.</p>
 *
 * <p>Base URL : {@code /api/v1/users}</p>
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retourne la liste paginée des utilisateurs de la succursale courante.
     *
     * @param page      numéro de page (0-indexé, défaut 0)
     * @param size      nombre d'éléments par page (défaut 20)
     * @param principal principal de sécurité de l'utilisateur connecté
     * @return page de {@link UserResponseDto}
     */
    @GetMapping
    @PreAuthorize("hasAuthority('manage-users')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        PageResponse<UserResponseDto> result = userService.findAll(page, size, principal.getBranchId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Retourne un utilisateur par son identifiant unique.
     *
     * @param id identifiant UUID de l'utilisateur
     * @return le DTO de l'utilisateur trouvé
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-users')")
    public ResponseEntity<ApiResponse<UserResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userService.findById(id)));
    }

    /**
     * Crée un nouvel utilisateur dans la succursale de l'utilisateur connecté.
     *
     * @param dto       données de création validées
     * @param principal principal de sécurité fournissant le branchId
     * @return le DTO de l'utilisateur créé avec le statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('manage-users')")
    public ResponseEntity<ApiResponse<UserResponseDto>> create(
            @Valid @RequestBody UserRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        UserResponseDto created = userService.create(dto, principal.getBranchId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Utilisateur créé avec succès", created));
    }

    /**
     * Met à jour les informations d'un utilisateur existant.
     *
     * @param id  identifiant UUID de l'utilisateur à modifier
     * @param dto nouvelles données validées
     * @return le DTO mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-users')")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Utilisateur mis à jour", userService.update(id, dto)));
    }

    /**
     * Supprime (soft-delete) un utilisateur par son identifiant.
     *
     * @param id identifiant UUID de l'utilisateur à supprimer
     * @return réponse vide avec message de confirmation
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-users')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé", null));
    }

    /**
     * Bascule le statut actif/inactif d'un utilisateur.
     * La désactivation déconnecte également l'utilisateur et désactive le 2FA.
     *
     * @param id identifiant UUID de l'utilisateur
     * @return réponse vide avec message de confirmation
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('manage-users')")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable UUID id) {
        userService.toggleStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Statut mis à jour", null));
    }

    /**
     * Met à jour le mot de passe d'un utilisateur après vérification de l'ancien.
     *
     * @param id      identifiant UUID de l'utilisateur
     * @param request objet contenant l'ancien et le nouveau mot de passe
     * @return réponse vide avec message de confirmation
     */
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAuthority('manage-users')")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
        return ResponseEntity.ok(ApiResponse.success("Mot de passe mis à jour", null));
    }
}
