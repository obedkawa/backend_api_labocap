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
 * Contrôleur REST exposant les opérations CRUD sur les médecins prescripteurs.
 * <p>
 * Les données sont filtrées par agence via le principal authentifié.
 * La consultation nécessite l'autorité {@code view-doctors} ;
 * la création, la modification et la suppression nécessitent {@code manage-doctors}.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Retourne la liste paginée des médecins de l'agence de l'utilisateur connecté.
     *
     * @param page      numéro de page (commence à 0)
     * @param size      nombre d'éléments par page (défaut : 20)
     * @param principal principal de l'utilisateur authentifié
     * @return page de {@link DoctorResponseDto}
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-doctors')")
    public ResponseEntity<ApiResponse<PageResponse<DoctorResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(doctorService.findAll(page, size, principal.getBranchId())));
    }

    /**
     * Recherche des médecins par nom (partiel, insensible à la casse) dans l'agence
     * de l'utilisateur connecté.
     *
     * @param q         terme de recherche
     * @param principal principal de l'utilisateur authentifié
     * @return liste des médecins correspondants
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('view-doctors')")
    public ResponseEntity<ApiResponse<List<DoctorResponseDto>>> search(
            @RequestParam String q,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(doctorService.search(q, principal.getBranchId())));
    }

    /**
     * Retourne un médecin par son identifiant unique.
     *
     * @param id identifiant UUID du médecin
     * @return le DTO du médecin trouvé
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-doctors')")
    public ResponseEntity<ApiResponse<DoctorResponseDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(doctorService.findById(id)));
    }

    /**
     * Crée un nouveau médecin rattaché à l'agence de l'utilisateur connecté.
     *
     * @param dto       données du médecin à créer
     * @param principal principal de l'utilisateur authentifié
     * @return le DTO du médecin créé avec le statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('manage-doctors')")
    public ResponseEntity<ApiResponse<DoctorResponseDto>> create(
            @Valid @RequestBody DoctorRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        DoctorResponseDto created = doctorService.create(dto, principal.getBranchId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Médecin créé", created));
    }

    /**
     * Met à jour les informations d'un médecin existant.
     *
     * @param id  identifiant du médecin à modifier
     * @param dto nouvelles données
     * @return le DTO du médecin mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-doctors')")
    public ResponseEntity<ApiResponse<DoctorResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody DoctorRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Médecin mis à jour", doctorService.update(id, dto)));
    }

    /**
     * Supprime (logiquement) un médecin.
     *
     * @param id identifiant du médecin à supprimer
     * @return réponse vide confirmant la suppression
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-doctors')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        doctorService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Médecin supprimé", null));
    }
}
