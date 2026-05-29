package com.labo.anapath.test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link UnitMeasurement}.
 *
 * <p>Les requêtes sont automatiquement filtrées par {@code deleted_at IS NULL}
 * grâce à la restriction Hibernate définie sur l'entité.</p>
 */
@Repository
public interface UnitMeasurementRepository extends JpaRepository<UnitMeasurement, UUID> {

    /**
     * Retourne la liste paginée des unités de mesure d'une succursale donnée.
     *
     * @param branchId identifiant de la succursale
     * @param pageable paramètres de pagination et de tri
     * @return page d'unités de mesure
     */
    Page<UnitMeasurement> findByBranchId(UUID branchId, Pageable pageable);

    List<UnitMeasurement> findAllByBranchIdOrderByName(UUID branchId);
}
