package com.labo.anapath.doctor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'accès aux données des hôpitaux ({@link Hospital}).
 * <p>
 * Toutes les requêtes sont filtrées par {@code branchId} pour garantir l'isolation
 * des données par agence.
 * </p>
 */
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, UUID> {

    /**
     * Retourne les hôpitaux d'une agence donnée, paginés.
     *
     * @param branchId identifiant de l'agence
     * @param pageable paramètres de pagination et de tri
     * @return page d'hôpitaux
     */
    Page<Hospital> findByBranchId(UUID branchId, Pageable pageable);

    /**
     * Recherche un hôpital par son identifiant et son agence.
     * Assure l'isolation multi-tenant.
     *
     * @param id       identifiant UUID de l'hôpital
     * @param branchId identifiant de l'agence
     * @return l'hôpital s'il appartient à l'agence, sinon vide
     */
    java.util.Optional<Hospital> findByIdAndBranchId(UUID id, UUID branchId);

    /**
     * Vérifie qu'aucun hôpital de la même agence n'a déjà ce nom.
     *
     * @param name     nom à vérifier (insensible à la casse)
     * @param branchId identifiant de l'agence
     * @return {@code true} si un doublon existe
     */
    boolean existsByNameIgnoreCaseAndBranchId(String name, UUID branchId);

    /**
     * Vérifie l'unicité du nom au sein d'une agence, en excluant l'hôpital identifié.
     *
     * @param name     nom à vérifier
     * @param branchId identifiant de l'agence
     * @param id       identifiant de l'hôpital à exclure
     * @return {@code true} si un autre hôpital de l'agence porte déjà ce nom
     */
    boolean existsByNameIgnoreCaseAndBranchIdAndIdNot(String name, UUID branchId, UUID id);

    /**
     * Recherche les hôpitaux d'une agence dont le nom contient la chaîne fournie
     * (insensible à la casse).
     *
     * @param name     terme de recherche partiel
     * @param branchId identifiant de l'agence
     * @return liste des hôpitaux correspondants
     */
    List<Hospital> findByNameContainingIgnoreCaseAndBranchId(String name, UUID branchId);
}
