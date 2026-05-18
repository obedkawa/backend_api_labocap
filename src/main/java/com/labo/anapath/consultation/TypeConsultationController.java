package com.labo.anapath.consultation;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contrôleur REST gérant les types de consultation médicale.
 * <p>
 * Toutes les routes sont préfixées par {@code /api/v1/type-consultations}.
 * Le contrôleur gère directement le repository car il n'y a pas de couche
 * service dédiée pour cette entité simple.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/type-consultations")
@RequiredArgsConstructor
public class TypeConsultationController {

    private final TypeConsultationRepository typeConsultationRepository;

    /** DTO de réponse interne représentant un type de consultation. */
    record TypeConsultationResponseDto(UUID id, String name, UUID branchId, LocalDateTime createdAt) {}

    /** DTO de requête interne pour la création ou la mise à jour d'un type de consultation. */
    @Getter
    @Setter
    static class TypeConsultationRequestDto {
        @NotBlank
        private String name;
    }

    /**
     * Retourne la liste paginée des types de consultation de la filiale connectée.
     *
     * @param page      numéro de page
     * @param size      taille de la page
     * @param principal utilisateur authentifié
     * @return page de types de consultation
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-consultations')")
    public ResponseEntity<ApiResponse<PageResponse<TypeConsultationResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                typeConsultationRepository.findByBranchId(principal.getBranchId(),
                        PageRequest.of(page, size)).map(tc -> new TypeConsultationResponseDto(
                        tc.getId(), tc.getName(), tc.getBranchId(), tc.getCreatedAt())))));
    }

    /**
     * Retourne un type de consultation par son identifiant.
     *
     * @param id identifiant UUID du type
     * @return le type de consultation correspondant
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-consultations')")
    public ResponseEntity<ApiResponse<TypeConsultationResponseDto>> findById(@PathVariable UUID id) {
        TypeConsultation tc = typeConsultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type consultation", id));
        return ResponseEntity.ok(ApiResponse.success(
                new TypeConsultationResponseDto(tc.getId(), tc.getName(), tc.getBranchId(), tc.getCreatedAt())));
    }

    /**
     * Crée un nouveau type de consultation pour la filiale de l'utilisateur connecté.
     *
     * @param dto       données du type à créer
     * @param principal utilisateur authentifié
     * @return le type créé avec le statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('create-type-consultations')")
    @Transactional
    public ResponseEntity<ApiResponse<TypeConsultationResponseDto>> create(
            @Valid @RequestBody TypeConsultationRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        TypeConsultation tc = new TypeConsultation();
        tc.setBranchId(principal.getBranchId());
        tc.setName(dto.getName());
        tc.setSlug(toSlug(dto.getName()));
        TypeConsultation saved = typeConsultationRepository.save(tc);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Type créé", new TypeConsultationResponseDto(
                        saved.getId(), saved.getName(), saved.getBranchId(), saved.getCreatedAt())));
    }

    /**
     * Met à jour le nom d'un type de consultation existant.
     *
     * @param id  identifiant UUID du type à modifier
     * @param dto nouvelles données
     * @return le type mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-type-consultations')")
    @Transactional
    public ResponseEntity<ApiResponse<TypeConsultationResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody TypeConsultationRequestDto dto) {
        TypeConsultation tc = typeConsultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type consultation", id));
        tc.setName(dto.getName());
        tc.setSlug(toSlug(dto.getName()));
        TypeConsultation saved = typeConsultationRepository.save(tc);
        return ResponseEntity.ok(ApiResponse.success("Type mis à jour", new TypeConsultationResponseDto(
                saved.getId(), saved.getName(), saved.getBranchId(), saved.getCreatedAt())));
    }

    /**
     * Supprime un type de consultation par son identifiant.
     *
     * @param id identifiant UUID du type à supprimer
     * @return réponse vide confirmant la suppression
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete-type-consultations')")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        typeConsultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type consultation", id));
        typeConsultationRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Type supprimé", null));
    }

    private String toSlug(String name) {
        return name.toLowerCase().trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
