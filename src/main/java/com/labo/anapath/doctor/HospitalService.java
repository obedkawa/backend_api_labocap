package com.labo.anapath.doctor;

import com.labo.anapath.common.dto.PageResponse;

import java.util.List;
import java.util.UUID;

/**
 * Contrat de service pour la gestion des hôpitaux et structures sanitaires.
 * <p>
 * Toutes les opérations de liste et de création sont filtrées par agence.
 * </p>
 */
public interface HospitalService {

    /**
     * Retourne la liste paginée des hôpitaux d'une agence.
     *
     * @param page     numéro de page (commence à 0)
     * @param size     nombre d'éléments par page
     * @param branchId identifiant de l'agence
     * @return page de {@link HospitalResponseDto}
     */
    PageResponse<HospitalResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Recherche un hôpital par son identifiant unique, en vérifiant qu'il appartient à l'agence.
     *
     * @param id       identifiant UUID de l'hôpital
     * @param branchId identifiant de l'agence de l'utilisateur connecté
     * @return le DTO de l'hôpital
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si l'hôpital n'existe pas ou n'appartient pas à l'agence
     */
    HospitalResponseDto findById(UUID id, UUID branchId);

    /**
     * Crée un nouvel hôpital rattaché à l'agence spécifiée.
     *
     * @param dto      données de l'hôpital
     * @param branchId identifiant de l'agence propriétaire
     * @return le DTO de l'hôpital créé
     * @throws com.labo.anapath.common.exception.DuplicateResourceException si le nom est déjà utilisé dans l'agence
     */
    HospitalResponseDto create(HospitalRequestDto dto, UUID branchId);

    /**
     * Met à jour un hôpital existant.
     *
     * @param id  identifiant de l'hôpital à modifier
     * @param dto nouvelles données
     * @return le DTO de l'hôpital mis à jour
     */
    HospitalResponseDto update(UUID id, HospitalRequestDto dto);

    /**
     * Supprime logiquement un hôpital.
     *
     * @param id identifiant de l'hôpital à supprimer
     */
    void delete(UUID id);

    /**
     * Recherche des hôpitaux par nom (partiel, insensible à la casse) dans une agence.
     *
     * @param q        terme de recherche
     * @param branchId identifiant de l'agence
     * @return liste des hôpitaux correspondants
     */
    List<HospitalResponseDto> search(String q, UUID branchId);
}
