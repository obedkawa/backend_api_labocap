package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

/**
 * Contrat de service pour la gestion des paiements de factures.
 *
 * <p>Permet d'enregistrer les encaissements, de les consulter et de les annuler.
 * La mise à jour n'est pas exposée : un paiement incorrect doit faire l'objet
 * d'une suppression suivie d'un nouvel enregistrement, ou d'une demande de
 * remboursement via {@link RefundRequestRepository}.</p>
 */
public interface PaymentService {

    /**
     * Retourne la liste paginée des paiements d'une agence.
     *
     * @param page     numéro de page (0-indexé)
     * @param size     nombre d'éléments par page
     * @param branchId identifiant de l'agence
     * @return page de paiements
     */
    PageResponse<PaymentResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Retourne le détail d'un paiement par son identifiant.
     *
     * @param id identifiant du paiement
     * @return le paiement trouvé
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si introuvable
     */
    PaymentResponseDto findById(UUID id);

    /**
     * Enregistre un nouveau paiement sur une facture existante.
     *
     * @param dto      données du paiement
     * @param branchId identifiant de l'agence
     * @return le paiement créé
     */
    PaymentResponseDto create(PaymentRequestDto dto, UUID branchId);

    /**
     * Supprime un paiement (annulation d'encaissement).
     *
     * @param id identifiant du paiement à supprimer
     */
    void delete(UUID id);
}
