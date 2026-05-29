package com.labo.anapath.patient;

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
 * Contrôleur REST exposant les opérations CRUD sur les dossiers patients.
 * <p>
 * Les données sont filtrées par agence via le principal authentifié. Les autorités
 * sont granulaires : {@code view-patients}, {@code create-patients}, {@code edit-patients}
 * et {@code delete-patients}, permettant un contrôle d'accès fin.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Retourne la liste paginée des patients de l'agence, avec recherche optionnelle.
     * La recherche porte sur le prénom, le nom, le téléphone et le code patient.
     *
     * @param page      numéro de page (commence à 0)
     * @param size      nombre d'éléments par page (défaut : 20)
     * @param search    terme de recherche optionnel (prénom, nom, téléphone, code)
     * @param principal principal de l'utilisateur authentifié
     * @return page de {@link PatientResponseDto}
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-patients')")
    public ResponseEntity<ApiResponse<PageResponse<PatientResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserPrincipal principal) {
        PageResponse<PatientResponseDto> result = patientService.findAll(page, size, search, principal.getBranchId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Retourne un patient par son identifiant unique.
     *
     * @param id        identifiant UUID du patient
     * @param principal principal de l'utilisateur authentifié
     * @return le DTO du patient trouvé
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-patients')")
    public ResponseEntity<ApiResponse<PatientResponseDto>> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(patientService.findById(id, principal.getBranchId())));
    }

    /**
     * Crée un nouveau dossier patient rattaché à l'agence de l'utilisateur connecté.
     *
     * @param dto       données du patient à créer (code, prénom, nom obligatoires)
     * @param principal principal de l'utilisateur authentifié
     * @return le DTO du patient créé avec le statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('create-patients')")
    public ResponseEntity<ApiResponse<PatientResponseDto>> create(
            @Valid @RequestBody PatientRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        PatientResponseDto created = patientService.create(dto, principal.getBranchId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Patient créé avec succès", created));
    }

    /**
     * Met à jour le dossier d'un patient existant.
     *
     * @param id        identifiant du patient à modifier
     * @param dto       nouvelles données
     * @param principal principal de l'utilisateur authentifié
     * @return le DTO du patient mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-patients')")
    public ResponseEntity<ApiResponse<PatientResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Patient mis à jour", patientService.update(id, dto, principal.getBranchId())));
    }

    /**
     * Supprime (logiquement) le dossier d'un patient.
     * Refusé si le patient possède des demandes d'examen.
     *
     * @param id        identifiant du patient à supprimer
     * @param principal principal de l'utilisateur authentifié
     * @return réponse vide confirmant la suppression
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete-patients')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        patientService.delete(id, principal.getBranchId());
        return ResponseEntity.ok(ApiResponse.success("Patient supprimé", null));
    }

    /**
     * Retourne le profil complet d'un patient : informations personnelles, résumé
     * des demandes d'examen et résumé des factures.
     *
     * @param id        identifiant du patient
     * @param principal principal de l'utilisateur authentifié
     * @return le {@link PatientProfileDto} agrégé
     */
    @GetMapping("/{id}/profile")
    @PreAuthorize("hasAuthority('view-patients')")
    public ResponseEntity<ApiResponse<PatientProfileDto>> getProfile(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(patientService.getProfile(id, principal.getBranchId())));
    }
}
