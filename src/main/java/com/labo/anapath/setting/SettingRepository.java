package com.labo.anapath.setting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'accès aux paramètres de configuration.
 */
@Repository
public interface SettingRepository extends JpaRepository<Setting, UUID> {

    /**
     * Retourne une page de paramètres filtrés par filiale.
     *
     * @param branchId identifiant de la filiale
     * @param pageable paramètres de pagination
     * @return page de paramètres
     */
    Page<Setting> findByBranchId(UUID branchId, Pageable pageable);

    /**
     * Recherche un paramètre par sa clé et sa filiale.
     * Utilisé pour l'opération upsert afin de déterminer si le paramètre existe déjà.
     *
     * @param key      clé du paramètre
     * @param branchId identifiant de la filiale
     * @return paramètre correspondant si présent
     */
    Optional<Setting> findByKeyAndBranchId(String key, UUID branchId);
}
