package com.labo.anapath.finance;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Contrôleur REST exposant les endpoints de gestion des paiements.
 *
 * <p>Base URL : {@code /api/v1/payments}</p>
 *
 * <ul>
 *   <li>Consultation : autorité {@code view-finance}</li>
 *   <li>Création / suppression : autorité {@code manage-payments}</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MobileMoneyService mobileMoneyService;

    /**
     * Retourne la liste paginée des paiements de l'agence de l'utilisateur connecté.
     *
     * @param page      numéro de page (défaut : 0)
     * @param size      taille de page (défaut : 20)
     * @param principal utilisateur authentifié
     * @return page de paiements
     */
    @GetMapping
    @PreAuthorize("hasAuthority('view-invoices')")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.findAll(page, size, principal.getBranchId())));
    }

    /**
     * Retourne le détail d'un paiement par son identifiant.
     *
     * @param id        identifiant du paiement
     * @param principal utilisateur authentifié (fournit le branchId)
     * @return le paiement correspondant
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view-invoices')")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.findById(id, principal.getBranchId())));
    }

    /**
     * Enregistre un nouveau paiement sur une facture.
     *
     * @param dto       données du paiement
     * @param principal utilisateur authentifié (fournit le branchId)
     * @return le paiement créé avec statut HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasAuthority('edit-invoices')")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> create(
            @Valid @RequestBody PaymentRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Paiement enregistré", paymentService.create(dto, principal.getBranchId())));
    }

    /**
     * Supprime un paiement (annulation d'encaissement).
     *
     * @param id identifiant du paiement à supprimer
     * @return réponse vide avec message de confirmation
     */
    @PostMapping("/initiate")
    @PreAuthorize("hasAuthority('edit-invoices')")
    public ResponseEntity<ApiResponse<MobileMoneyStatusResponseDto>> initiatePayment(
            @Valid @RequestBody MobileMoneyInitiateRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(mobileMoneyService.initiate(dto, principal.getBranchId())));
    }

    @GetMapping("/{id}/status")
    @PreAuthorize("hasAuthority('view-invoices')")
    public ResponseEntity<ApiResponse<MobileMoneyStatusResponseDto>> checkPaymentStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(mobileMoneyService.checkStatus(id, principal.getBranchId())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('edit-invoices')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Paiement supprimé", null));
    }
}
