package com.labo.anapath.client;

import com.labo.anapath.common.dto.PageResponse;

import java.util.List;
import java.util.UUID;

/**
 * Contrat de service pour la gestion des clients institutionnels.
 * <p>
 * Toutes les opérations de liste et de création prennent un {@code branchId}
 * pour garantir l'isolation des données par agence. La mise à jour passe également
 * le {@code branchId} afin de vérifier que le client appartient bien à l'agence
 * de l'utilisateur qui effectue la modification.
 * </p>
 */
public interface ClientService {

    /**
     * Retourne la liste paginée des clients d'une agence.
     *
     * @param page     numéro de page (commence à 0)
     * @param size     nombre d'éléments par page
     * @param branchId identifiant de l'agence
     * @return page de {@link ClientResponseDto}
     */
    PageResponse<ClientResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Recherche un client par son identifiant unique.
     *
     * @param id identifiant UUID du client
     * @return le DTO du client
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si le client n'existe pas
     */
    ClientResponseDto findById(UUID id);

    /**
     * Crée un nouveau client rattaché à l'agence spécifiée.
     *
     * @param dto      données du client (nom obligatoire)
     * @param branchId identifiant de l'agence propriétaire
     * @return le DTO du client créé
     * @throws com.labo.anapath.common.exception.DuplicateResourceException si le nom ou l'IFU est déjà utilisé
     */
    ClientResponseDto create(ClientRequestDto dto, UUID branchId);

    /**
     * Met à jour un client existant.
     * Vérifie que le client appartient à l'agence de l'utilisateur demandeur.
     *
     * @param id       identifiant du client à modifier
     * @param dto      nouvelles données
     * @param branchId identifiant de l'agence de l'utilisateur (contrôle d'appartenance)
     * @return le DTO du client mis à jour
     */
    ClientResponseDto update(UUID id, ClientRequestDto dto, UUID branchId);

    /**
     * Supprime logiquement un client.
     * Refusé si le client possède des contrats liés.
     *
     * @param id identifiant du client à supprimer
     */
    void delete(UUID id);

    /**
     * Recherche des clients par nom (partiel, insensible à la casse) dans une agence.
     *
     * @param q        terme de recherche
     * @param branchId identifiant de l'agence
     * @return liste des clients correspondants
     */
    List<ClientResponseDto> search(String q, UUID branchId);
}
