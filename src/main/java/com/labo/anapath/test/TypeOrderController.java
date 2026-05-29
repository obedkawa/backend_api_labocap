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

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur REST gérant les opérations CRUD sur les types de bons de demande.
 *
 * <p>La consultation est accessible aux utilisateurs ayant la permission {@code view-tests},
 * tandis que la création, la modification et la suppression nécessitent {@code manage-tests}.</p>
 *
 * <p>Base URL : {@code /api/v1/type-orders}</p>
 */
@RestController
@RequestMapping("/api/v1/type-orders")
@RequiredArgsConstructor
public class TypeOrderController {

    private final TypeOrderService typeOrderService;

    /**
     * Retourne la liste paginée des types de bons de la succursale courante.
     *
     * @param page      numéro de page (0-indexé, défaut 0)
     * @param size      nombre d'éléments par page (défaut 20)
     * @param principal principal de sécurité de l'utilisateur connecté
     * @return page de {@link TypeOrderResponseDto}
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-tests')")
    public ResponseEntity<ApiResponse<PageResponse<TypeOrderResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(typeOrderService.findAll(page, size, principal.getBranchId())));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('view-tests')")
    public ResponseEntity<ApiResponse<List<TypeOrderResponseDto>>> findAll(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(typeOrderService.findAll(principal.getBranchId())));
    }

    /**
     * Retourne un type de bon par son identifiant unique.
     *
     * @param id identifiant UUID du type de bon
     * @return le DTO du type trouvé
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-tests')")
    public ResponseEntity<ApiResponse<TypeOrderResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(typeOrderService.findById(id)));
    }

    /**
     * Crée un nouveau type de bon dans la succursale de l'utilisateur connecté.
     *
     * @param dto       données de création validées (titre et slug obligatoires)
     * @param principal principal de sécurité fournissant le branchId
     * @return le DTO du type créé avec le statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('edit-tests')")
    public ResponseEntity<ApiResponse<TypeOrderResponseDto>> create(
            @Valid @RequestBody TypeOrderRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Type créé", typeOrderService.create(dto, principal.getBranchId())));
    }

    /**
     * Met à jour un type de bon existant.
     *
     * @param id  identifiant UUID du type à modifier
     * @param dto nouvelles données validées
     * @return le DTO mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-tests')")
    public ResponseEntity<ApiResponse<TypeOrderResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody TypeOrderRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Type mis à jour", typeOrderService.update(id, dto)));
    }

    /**
     * Supprime (soft-delete) un type de bon.
     *
     * @param id identifiant UUID du type à supprimer
     * @return réponse vide avec message de confirmation
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-tests')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        typeOrderService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Type supprimé", null));
    }
}
