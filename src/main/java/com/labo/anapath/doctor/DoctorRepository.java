package com.labo.anapath.doctor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'accès aux données des médecins ({@link Doctor}).
 * <p>
 * Les requêtes de liste et de recherche sont systématiquement filtrées par {@code branchId}
 * pour respecter l'isolation des données par agence. La recherche par nom utilise une
 * requête JPQL explicite afin de gérer la correspondance partielle insensible à la casse.
 * </p>
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    /**
     * Retourne les médecins d'une agence donnée, paginés.
     *
     * @param branchId identifiant de l'agence
     * @param pageable paramètres de pagination et de tri
     * @return page de médecins
     */
    Page<Doctor> findByBranchId(UUID branchId, Pageable pageable);

    /**
     * Vérifie qu'aucun médecin de la même agence n'a déjà ce nom.
     *
     * @param name     nom à vérifier (insensible à la casse)
     * @param branchId identifiant de l'agence
     * @return {@code true} si un doublon existe
     */
    boolean existsByNameIgnoreCaseAndBranchId(String name, UUID branchId);

    /**
     * Vérifie l'unicité du nom au sein d'une agence en excluant le médecin identifié.
     *
     * @param name     nom à vérifier
     * @param branchId identifiant de l'agence
     * @param id       identifiant du médecin à exclure
     * @return {@code true} si un autre médecin de l'agence porte déjà ce nom
     */
    boolean existsByNameIgnoreCaseAndBranchIdAndIdNot(String name, UUID branchId, UUID id);

    /**
     * Recherche des médecins par nom (partiel, insensible à la casse) dans une agence.
     * Utilise une requête JPQL explicite pour le LIKE insensible à la casse.
     *
     * @param q        terme de recherche
     * @param branchId identifiant de l'agence
     * @return liste des médecins dont le nom contient le terme
     */
    @Query("SELECT d FROM Doctor d WHERE d.branchId = :branchId " +
           "AND LOWER(d.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Doctor> searchByNameAndBranchId(@Param("q") String q, @Param("branchId") UUID branchId);
}
