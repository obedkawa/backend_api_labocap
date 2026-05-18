package com.labo.anapath.consultation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'accès aux types de consultation médicale.
 */
@Repository
public interface TypeConsultationRepository extends JpaRepository<TypeConsultation, UUID> {

    /**
     * Retourne une page de types de consultation pour une filiale donnée.
     *
     * @param branchId identifiant de la filiale
     * @param pageable paramètres de pagination
     * @return page de types de consultation
     */
    Page<TypeConsultation> findByBranchId(UUID branchId, Pageable pageable);
}
