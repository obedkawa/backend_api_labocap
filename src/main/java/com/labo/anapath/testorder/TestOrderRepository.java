package com.labo.anapath.testorder;

import com.labo.anapath.patient.Patient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'entité {@link TestOrder}.
 *
 * <p>Étend {@link JpaSpecificationExecutor} pour permettre les requêtes dynamiques
 * via {@link TestOrderSpecification}.
 */
@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder, UUID>, JpaSpecificationExecutor<TestOrder> {

    /**
     * Vérifie si un bon d'examen existe pour le patient donné.
     * Utilisé pour contrôler l'existence d'antécédents avant suppression du patient.
     *
     * @param patient le patient à vérifier
     * @return {@code true} si au moins un bon existe pour ce patient
     */
    boolean existsByPatient(Patient patient);

    /**
     * Récupère un bon d'examen par son identifiant et sa branche.
     * Assure l'isolation multi-tenant : un bon ne peut être accédé que par sa branche propriétaire.
     *
     * @param id       identifiant UUID du bon
     * @param branchId identifiant de la branche
     * @return le bon s'il appartient à la branche, sinon {@link java.util.Optional#empty()}
     */
    Optional<TestOrder> findByIdAndBranchId(UUID id, UUID branchId);

    /**
     * Retourne tous les bons d'examen d'un patient, triés du plus récent au plus ancien.
     *
     * @param patient le patient concerné
     * @return liste ordonnée par date de création décroissante
     */
    List<TestOrder> findByPatientOrderByCreatedAtDesc(Patient patient);

    /**
     * Récupère les bons d'examen d'une branche ayant un code généré pour une année donnée,
     * triés par code décroissant. Utilisé par l'algorithme de génération de code
     * pour déterminer le dernier numéro de séquence de l'année.
     *
     * @param branchId identifiant de la branche
     * @param year     année civile (ex. 2026)
     * @param pageable pagination (en pratique : page 0, taille 1 pour obtenir le dernier)
     * @return liste des bons correspondants
     */
    @Query("SELECT t FROM TestOrder t WHERE t.branchId = :branchId AND t.code IS NOT NULL " +
           "AND YEAR(t.createdAt) = :year ORDER BY t.code DESC")
    List<TestOrder> findByBranchIdAndCodeNotNullAndYear(
            @Param("branchId") UUID branchId,
            @Param("year") int year,
            Pageable pageable);
}
