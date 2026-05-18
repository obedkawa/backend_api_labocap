package com.labo.anapath.hr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'accès aux demandes de congé des employés.
 */
@Repository
public interface EmployeeTimeoffRepository extends JpaRepository<EmployeeTimeoff, UUID> {

    /**
     * Retourne une page de demandes de congé filtrées par identifiant d'employé.
     *
     * @param employeeId identifiant UUID de l'employé
     * @param pageable   paramètres de pagination
     * @return page de demandes de congé
     */
    Page<EmployeeTimeoff> findByEmployeeId(UUID employeeId, Pageable pageable);
}
