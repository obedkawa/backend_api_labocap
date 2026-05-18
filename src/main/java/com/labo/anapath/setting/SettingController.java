package com.labo.anapath.setting;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Contrôleur REST gérant la configuration applicative du laboratoire.
 * <p>
 * Toutes les routes sont préfixées par {@code /api/v1/settings}.
 * L'opération de création/modification est unifiée via un upsert
 * (création si la clé n'existe pas, mise à jour sinon).
 * </p>
 */
@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    /**
     * Retourne la liste paginée des paramètres de la filiale connectée.
     * La taille par défaut est volontairement grande (50) pour charger
     * tous les paramètres en une seule requête depuis le front.
     *
     * @param page      numéro de page
     * @param size      taille de la page
     * @param principal utilisateur authentifié
     * @return page de paramètres
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-settings')")
    public ResponseEntity<ApiResponse<PageResponse<SettingResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(settingService.findAll(page, size, principal.getBranchId())));
    }

    /**
     * Retourne un paramètre par son identifiant.
     *
     * @param id identifiant UUID du paramètre
     * @return le paramètre correspondant
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-settings')")
    public ResponseEntity<ApiResponse<SettingResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(settingService.findById(id)));
    }

    /**
     * Crée ou met à jour un paramètre (upsert) pour la filiale connectée.
     * Si un paramètre avec la même clé existe déjà, sa valeur est mise à jour.
     *
     * @param dto       données du paramètre (clé + valeur)
     * @param principal utilisateur authentifié
     * @return le paramètre sauvegardé
     */
    @PostMapping
    @PreAuthorize("hasAuthority('manage-settings')")
    public ResponseEntity<ApiResponse<SettingResponseDto>> upsert(
            @Valid @RequestBody SettingRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Paramètre sauvegardé", settingService.upsert(dto, principal.getBranchId())));
    }

    /**
     * Supprime (logiquement) un paramètre par son identifiant.
     *
     * @param id identifiant UUID du paramètre à supprimer
     * @return réponse vide confirmant la suppression
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-settings')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        settingService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Paramètre supprimé", null));
    }
}
