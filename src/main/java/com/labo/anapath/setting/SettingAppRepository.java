package com.labo.anapath.setting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettingAppRepository extends JpaRepository<SettingApp, UUID> {
    Page<SettingApp> findByBranchId(UUID branchId, Pageable pageable);
    Optional<SettingApp> findByKeyAndBranchId(String key, UUID branchId);

    /**
     * Recherche un setting global par sa clé (sans filtre sur la branche).
     * Utilisé pour les settings transversaux (OurVoice, entête PDF…).
     */
    Optional<SettingApp> findByKey(String key);
}
