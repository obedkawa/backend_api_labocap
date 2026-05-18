package com.labo.anapath.test;

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

import java.util.UUID;

/**
 * Contrôleur REST gérant les opérations CRUD sur les codes de référence (DataCode).
 *
 * <p>La consultation est accessible aux utilisateurs ayant la permission {@code view-tests},
 * tandis que la création, la modification et la suppression nécessitent {@code manage-tests}.</p>
 *
 * <p>Base URL : {@code /api/v1/data-codes}</p>
 */
@RestController
@RequestMapping("/api/v1/data-codes")
@RequiredArgsConstructor
public class DataCodeController {

    private final DataCodeService dataCodeService;

    /**
     * Retourne la liste paginée des codes de référence de la succursale courante.
     *
     * @param page      numéro de page (0-indexé, défaut 0)
     * @param size      nombre d'éléments par page (défaut 20)
     * @param principal principal de sécurité de l'utilisateur connecté
     * @return page de {@link DataCodeResponseDto}
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-tests')")
    public ResponseEntity<ApiResponse<PageResponse<DataCodeResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(dataCodeService.findAll(page, size, principal.getBranchId())));
    }

    /**
     * Retourne un code de référence par son identifiant unique.
     *
     * @param id identifiant UUID du code
     * @return le DTO du code trouvé
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-tests')")
    public ResponseEntity<ApiResponse<DataCodeResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(dataCodeService.findById(id)));
    }

    /**
     * Crée un nouveau code de référence dans la succursale de l'utilisateur connecté.
     *
     * @param dto       données de création validées
     * @param principal principal de sécurité fournissant le branchId
     * @return le DTO du code créé avec le statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('manage-tests')")
    public ResponseEntity<ApiResponse<DataCodeResponseDto>> create(
            @Valid @RequestBody DataCodeRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Code créé", dataCodeService.create(dto, principal.getBranchId())));
    }

    /**
     * Met à jour un code de référence existant.
     *
     * @param id  identifiant UUID du code à modifier
     * @param dto nouvelles données validées
     * @return le DTO mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-tests')")
    public ResponseEntity<ApiResponse<DataCodeResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody DataCodeRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Code mis à jour", dataCodeService.update(id, dto)));
    }

    /**
     * Supprime (soft-delete) un code de référence.
     *
     * @param id identifiant UUID du code à supprimer
     * @return réponse vide avec message de confirmation
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-tests')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        dataCodeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Code supprimé", null));
    }
}
