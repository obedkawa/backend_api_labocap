package com.labo.anapath.report;

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
 * Contrôleur REST pour la gestion des tags de classification des comptes-rendus
 * ({@code /api/v1/tags}).
 *
 * <p>Les tags permettent de catégoriser les CRs par thématique anatomopathologique.
 * Le CRUD complet est exposé : liste paginée, consultation, création, mise à jour, suppression.
 * La logique est légère (pas de service dédié) : le repository est utilisé directement
 * depuis le contrôleur, conformément au pattern adopté pour les référentiels simples.
 */
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;

    /** DTO de réponse interne pour un tag. */
    record TagResponseDto(UUID id, String name, UUID branchId, LocalDateTime createdAt) {}

    /** DTO de requête interne pour la création ou mise à jour d'un tag. */
    @Getter
    @Setter
    static class TagRequestDto {
        @NotBlank(message = "Le nom du tag est obligatoire")
        private String name;
    }

    /**
     * Retourne la liste paginée des tags de la branche de l'utilisateur connecté.
     *
     * @param page      numéro de page (0-based, défaut 0)
     * @param size      taille de la page (défaut 20)
     * @param principal principal Spring Security contenant le branchId
     * @return page de {@link TagResponseDto}
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-reports')")
    public ResponseEntity<ApiResponse<PageResponse<TagResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                tagRepository.findByBranchId(principal.getBranchId(),
                        PageRequest.of(page, size)).map(t -> new TagResponseDto(
                        t.getId(), t.getName(), t.getBranchId(), t.getCreatedAt())))));
    }

    /**
     * Retourne un tag par son identifiant.
     *
     * @param id identifiant UUID du tag
     * @return le tag correspondant, ou 404 s'il n'existe pas
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-reports')")
    public ResponseEntity<ApiResponse<TagResponseDto>> findById(@PathVariable UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
        return ResponseEntity.ok(ApiResponse.success(
                new TagResponseDto(tag.getId(), tag.getName(), tag.getBranchId(), tag.getCreatedAt())));
    }

    /**
     * Crée un nouveau tag pour la branche de l'utilisateur connecté.
     *
     * @param dto       données du tag ({@code name} obligatoire)
     * @param principal principal Spring Security contenant le branchId
     * @return le tag créé avec le code HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('manage-reports')")
    @Transactional
    public ResponseEntity<ApiResponse<TagResponseDto>> create(
            @Valid @RequestBody TagRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        Tag tag = new Tag();
        tag.setBranchId(principal.getBranchId());
        tag.setName(dto.getName());
        Tag saved = tagRepository.save(tag);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tag créé", new TagResponseDto(
                        saved.getId(), saved.getName(), saved.getBranchId(), saved.getCreatedAt())));
    }

    /**
     * Met à jour le nom d'un tag existant.
     *
     * @param id  identifiant UUID du tag
     * @param dto nouvelles données
     * @return le tag mis à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-reports')")
    @Transactional
    public ResponseEntity<ApiResponse<TagResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody TagRequestDto dto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
        tag.setName(dto.getName());
        Tag saved = tagRepository.save(tag);
        return ResponseEntity.ok(ApiResponse.success("Tag mis à jour", new TagResponseDto(
                saved.getId(), saved.getName(), saved.getBranchId(), saved.getCreatedAt())));
    }

    /**
     * Supprime un tag. Les comptes-rendus associés conservent leurs autres tags.
     *
     * @param id identifiant UUID du tag à supprimer
     * @return réponse vide 200 en cas de succès
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-reports')")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
        tagRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Tag supprimé", null));
    }
}
