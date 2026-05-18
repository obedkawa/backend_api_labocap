package com.labo.anapath.setting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'accès aux modèles de rapport PDF.
 */
@Repository
public interface SettingReportTemplateRepository extends JpaRepository<SettingReportTemplate, UUID> {

    /**
     * Retourne une page de modèles de rapport filtrés par filiale.
     *
     * @param branchId identifiant de la filiale
     * @param pageable paramètres de pagination
     * @return page de modèles de rapport
     */
    Page<SettingReportTemplate> findByBranchId(UUID branchId, Pageable pageable);
}
