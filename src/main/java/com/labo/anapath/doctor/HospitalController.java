package com.labo.anapath.doctor;

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
 * Contrôleur REST exposant les opérations CRUD sur les hôpitaux et structures sanitaires.
 * <p>
 * Les données sont filtrées par agence via le principal authentifié.
 * La consultation nécessite l'autorité {@code view-hospitals} ;
 * la création, la modification et la suppression nécessitent {@code manage-hospitals}.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    /**
     * Retourne la liste paginée des hôpitaux de l'agence de l'utilisateur connecté.
     *
     * @param page      numéro de page (commence à 0)
     * @param size      nombre d'éléments par page (défaut : 20)
     * @param principal principal de l'utilisateur authentifié
     * @return page de {@link HospitalResponseDto}
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-hospitals')")
    public ResponseEntity<ApiResponse<PageResponse<HospitalResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(hospitalService.findAll(page, size, principal.getBranchId())));
    }

    /**
     * Recherche des hôpitaux par nom (partiel, insensible à la casse) dans l'agence
     * de l'utilisateur connecté.
     *
     * @param q         terme de recherche
     * @param principal principal de l'utilisateur authentifié
     * @return liste des hôpitaux correspondants
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('view-hospitals')")
    public ResponseEntity<ApiResponse<List<HospitalResponseDto>>> search(
            @RequestParam String q,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(hospitalService.search(q, principal.getBranchId())));
    }

    /**
     * Retourne un hôpital par son identifiant unique.
     *
     * @param id identifiant UUID de l'hôpital
     * @return le DTO de l'hôpital trouvé
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-hospitals')")
    public ResponseEntity<ApiResponse<HospitalResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(hospitalService.findById(id)));
    }

    /**
     * Crée un nouvel hôpital rattaché à l'agence de l'utilisateur connecté.
     *
     * @param dto       données de l'hôpital à créer
     * @param principal principal de l'utilisateur authentifié
     * @return le DTO de l'hôpital créé avec le statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('manage-hospitals')")
    public ResponseEntity<ApiResponse<HospitalResponseDto>> create(
            @Valid @RequestBody HospitalRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        HospitalResponseDto created = hospitalService.create(dto, principal.getBranchId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Hôpital créé", created));
    }

    /**
     * Met à jour les informations d'un hôpital existant.
     *
     * @param id  identifiant de l'hôpital à modifier
     * @param dto nouvelles données
     * @return le DTO de l'hôpital mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-hospitals')")
    public ResponseEntity<ApiResponse<HospitalResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody HospitalRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Hôpital mis à jour", hospitalService.update(id, dto)));
    }

    /**
     * Supprime (logiquement) un hôpital.
     *
     * @param id identifiant de l'hôpital à supprimer
     * @return réponse vide confirmant la suppression
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-hospitals')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        hospitalService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Hôpital supprimé", null));
    }
}
