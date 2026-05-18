package com.labo.anapath.doctor;

import com.labo.anapath.common.dto.PageResponse;

import java.util.List;
import java.util.UUID;

/**
 * Contrat de service pour la gestion des médecins prescripteurs.
 * <p>
 * Les opérations de liste, de création et de recherche sont filtrées par agence.
 * La mise à jour n'exige pas le {@code branchId} car l'unicité du nom est vérifiée
 * à partir du {@code branchId} stocké sur l'entité elle-même.
 * </p>
 */
public interface DoctorService {

    /**
     * Retourne la liste paginée des médecins d'une agence.
     *
     * @param page     numéro de page (commence à 0)
     * @param size     nombre d'éléments par page
     * @param branchId identifiant de l'agence
     * @return page de {@link DoctorResponseDto}
     */
    PageResponse<DoctorResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Recherche un médecin par son identifiant unique.
     *
     * @param id identifiant UUID du médecin
     * @return le DTO du médecin
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si le médecin n'existe pas
     */
    DoctorResponseDto findById(UUID id);

    /**
     * Crée un nouveau médecin rattaché à l'agence spécifiée.
     *
     * @param dto      données du médecin (nom obligatoire)
     * @param branchId identifiant de l'agence propriétaire
     * @return le DTO du médecin créé
     * @throws com.labo.anapath.common.exception.DuplicateResourceException si le nom est déjà utilisé dans l'agence
     */
    DoctorResponseDto create(DoctorRequestDto dto, UUID branchId);

    /**
     * Met à jour un médecin existant.
     *
     * @param id  identifiant du médecin à modifier
     * @param dto nouvelles données
     * @return le DTO du médecin mis à jour
     */
    DoctorResponseDto update(UUID id, DoctorRequestDto dto);

    /**
     * Supprime logiquement un médecin.
     *
     * @param id identifiant du médecin à supprimer
     */
    void delete(UUID id);

    /**
     * Recherche des médecins par nom (partiel, insensible à la casse) dans une agence.
     *
     * @param q        terme de recherche
     * @param branchId identifiant de l'agence
     * @return liste des médecins correspondants
     */
    List<DoctorResponseDto> search(String q, UUID branchId);
}
