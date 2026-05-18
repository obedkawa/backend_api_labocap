package com.labo.anapath.hr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'accès aux contrats de travail des employés.
 */
@Repository
public interface EmployeeContratRepository extends JpaRepository<EmployeeContrat, UUID> {

    /**
     * Retourne une page de contrats filtrés par identifiant d'employé.
     *
     * @param employeeId identifiant UUID de l'employé
     * @param pageable   paramètres de pagination
     * @return page de contrats de l'employé
     */
    Page<EmployeeContrat> findByEmployeeId(UUID employeeId, Pageable pageable);
}
