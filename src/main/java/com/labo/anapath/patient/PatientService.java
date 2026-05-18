package com.labo.anapath.patient;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

/**
 * Contrat de service pour la gestion des dossiers patients.
 * <p>
 * Les opérations de liste et de création sont filtrées par agence. La méthode
 * {@link #getProfile(UUID)} agrège les données du patient avec ses demandes
 * d'examen et ses factures pour fournir une vue synthétique complète.
 * </p>
 */
public interface PatientService {

    /**
     * Retourne la liste paginée des patients d'une agence, avec recherche optionnelle.
     * Sans terme de recherche, tous les patients de l'agence sont retournés.
     *
     * @param page     numéro de page (commence à 0)
     * @param size     nombre d'éléments par page
     * @param search   terme de recherche optionnel (prénom, nom, téléphone, code)
     * @param branchId identifiant de l'agence
     * @return page de {@link PatientResponseDto}
     */
    PageResponse<PatientResponseDto> findAll(int page, int size, String search, UUID branchId);

    /**
     * Recherche un patient par son identifiant unique.
     *
     * @param id identifiant UUID du patient
     * @return le DTO du patient
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si le patient n'existe pas
     */
    PatientResponseDto findById(UUID id);

    /**
     * Crée un nouveau dossier patient rattaché à l'agence spécifiée.
     * Vérifie l'unicité du code et du numéro de téléphone au sein de l'agence.
     *
     * @param dto      données du patient (code, prénom, nom obligatoires)
     * @param branchId identifiant de l'agence propriétaire
     * @return le DTO du patient créé
     * @throws com.labo.anapath.common.exception.DuplicateResourceException si le code ou le téléphone est déjà utilisé
     */
    PatientResponseDto create(PatientRequestDto dto, UUID branchId);

    /**
     * Met à jour le dossier d'un patient existant.
     *
     * @param id  identifiant du patient à modifier
     * @param dto nouvelles données
     * @return le DTO du patient mis à jour
     */
    PatientResponseDto update(UUID id, PatientRequestDto dto);

    /**
     * Supprime logiquement le dossier d'un patient.
     * Refusé si le patient possède des demandes d'examen (TestOrder).
     *
     * @param id identifiant du patient à supprimer
     * @throws com.labo.anapath.common.exception.BusinessException si le patient a des demandes liées
     */
    void delete(UUID id);

    /**
     * Retourne le profil complet d'un patient, incluant un résumé de ses demandes
     * d'examen et de ses factures avec les totaux financiers calculés.
     *
     * @param id identifiant du patient
     * @return le {@link PatientProfileDto} agrégé
     */
    PatientProfileDto getProfile(UUID id);
}
