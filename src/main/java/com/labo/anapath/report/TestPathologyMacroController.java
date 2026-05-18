package com.labo.anapath.report;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
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
import java.util.List;
import java.util.UUID;

/**
 * Contrôleur REST pour la gestion des macros de texte anatomopathologiques
 * ({@code /api/v1/pathology-macros}).
 *
 * <p>Les macros sont des templates de texte prédéfinis que les pathologistes peuvent
 * insérer dans leurs comptes-rendus pour accélérer la rédaction. CRUD complet exposé.
 * La logique est directement dans le contrôleur (référentiel simple sans service dédié).
 */
@RestController
@RequestMapping("/api/v1/pathology-macros")
@RequiredArgsConstructor
@Validated
public class TestPathologyMacroController {

    private final TestPathologyMacroRepository testPathologyMacroRepository;

    /** DTO de réponse interne pour une macro anatomopathologique. */
    record TestPathologyMacroResponseDto(UUID id, String title, String content, UUID branchId, LocalDateTime createdAt) {}

    /** DTO de requête interne pour la création ou mise à jour d'une macro. */
    @Getter
    @Setter
    static class TestPathologyMacroRequestDto {
        @NotBlank(message = "Le titre de la macro est obligatoire")
        private String title;

        private String content;
    }

    /** DTO de requête pour la création en masse de macros. */
    @Getter
    @Setter
    static class BulkMacroRequestDto {
        @NotEmpty(message = "La liste de macros ne peut pas être vide")
        private List<@Valid TestPathologyMacroRequestDto> macros;
    }

    /**
     * Retourne la liste paginée des macros de la branche de l'utilisateur connecté.
     *
     * @param page      numéro de page (0-based, défaut 0)
     * @param size      taille de la page (défaut 20)
     * @param principal principal Spring Security contenant le branchId
     * @return page de {@link TestPathologyMacroResponseDto}
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-reports')")
    public ResponseEntity<ApiResponse<PageResponse<TestPathologyMacroResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(
                testPathologyMacroRepository.findByBranchId(principal.getBranchId(),
                        PageRequest.of(page, size)).map(m -> new TestPathologyMacroResponseDto(
                        m.getId(), m.getTitle(), m.getContent(), m.getBranchId(), m.getCreatedAt())))));
    }

    /**
     * Retourne une macro par son identifiant.
     *
     * @param id identifiant UUID de la macro
     * @return la macro correspondante, ou 404 si elle n'existe pas
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-reports')")
    public ResponseEntity<ApiResponse<TestPathologyMacroResponseDto>> findById(@PathVariable UUID id) {
        TestPathologyMacro m = testPathologyMacroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Macro pathologie", id));
        return ResponseEntity.ok(ApiResponse.success(
                new TestPathologyMacroResponseDto(m.getId(), m.getTitle(), m.getContent(), m.getBranchId(), m.getCreatedAt())));
    }

    /**
     * Crée une nouvelle macro pour la branche de l'utilisateur connecté.
     *
     * @param dto       données de la macro (titre obligatoire, contenu optionnel)
     * @param principal principal Spring Security contenant le branchId
     * @return la macro créée avec le code HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('manage-reports')")
    @Transactional
    public ResponseEntity<ApiResponse<TestPathologyMacroResponseDto>> create(
            @Valid @RequestBody TestPathologyMacroRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        TestPathologyMacro m = new TestPathologyMacro();
        m.setBranchId(principal.getBranchId());
        m.setTitle(dto.getTitle());
        m.setContent(dto.getContent());
        TestPathologyMacro saved = testPathologyMacroRepository.save(m);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Macro créée", new TestPathologyMacroResponseDto(
                        saved.getId(), saved.getTitle(), saved.getContent(), saved.getBranchId(), saved.getCreatedAt())));
    }

    /**
     * Met à jour le titre et le contenu d'une macro existante.
     *
     * @param id  identifiant UUID de la macro
     * @param dto nouvelles données
     * @return la macro mise à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-reports')")
    @Transactional
    public ResponseEntity<ApiResponse<TestPathologyMacroResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody TestPathologyMacroRequestDto dto) {
        TestPathologyMacro m = testPathologyMacroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Macro pathologie", id));
        m.setTitle(dto.getTitle());
        m.setContent(dto.getContent());
        TestPathologyMacro saved = testPathologyMacroRepository.save(m);
        return ResponseEntity.ok(ApiResponse.success("Macro mise à jour", new TestPathologyMacroResponseDto(
                saved.getId(), saved.getTitle(), saved.getContent(), saved.getBranchId(), saved.getCreatedAt())));
    }

    /**
     * Crée plusieurs macros en une seule transaction.
     *
     * @param dto       liste des macros à créer
     * @param principal principal Spring Security contenant le branchId
     * @return liste des macros créées avec le code HTTP 201
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('manage-reports')")
    @Transactional
    public ResponseEntity<ApiResponse<List<TestPathologyMacroResponseDto>>> createBulk(
            @Valid @RequestBody BulkMacroRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<TestPathologyMacro> macros = dto.getMacros().stream().map(req -> {
            TestPathologyMacro m = new TestPathologyMacro();
            m.setBranchId(principal.getBranchId());
            m.setTitle(req.getTitle());
            m.setContent(req.getContent());
            return m;
        }).toList();
        List<TestPathologyMacroResponseDto> saved = testPathologyMacroRepository.saveAll(macros).stream()
                .map(m -> new TestPathologyMacroResponseDto(m.getId(), m.getTitle(), m.getContent(), m.getBranchId(), m.getCreatedAt()))
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Macros créées", saved));
    }

    /**
     * Recherche des macros par titre ou contenu (insensible à la casse).
     *
     * @param q         terme de recherche (min 2 caractères)
     * @param principal principal Spring Security contenant le branchId
     * @return liste de macros correspondantes (max 50)
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('view-reports')")
    public ResponseEntity<ApiResponse<List<TestPathologyMacroResponseDto>>> search(
            @RequestParam @Size(min = 2, message = "Le terme de recherche doit contenir au moins 2 caractères") String q,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<TestPathologyMacroResponseDto> results = testPathologyMacroRepository
                .search(principal.getBranchId(), q, PageRequest.of(0, 50)).stream()
                .map(m -> new TestPathologyMacroResponseDto(m.getId(), m.getTitle(), m.getContent(), m.getBranchId(), m.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * Supprime (soft delete) une macro.
     *
     * @param id identifiant UUID de la macro à supprimer
     * @return réponse vide 200 en cas de succès
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage-reports')")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        testPathologyMacroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Macro pathologie", id));
        testPathologyMacroRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Macro supprimée", null));
    }
}
