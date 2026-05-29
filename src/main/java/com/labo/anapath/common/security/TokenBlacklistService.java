package com.labo.anapath.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service de blacklist des tokens JWT révoqués après déconnexion.
 * <p>
 * Persiste les tokens révoqués en base de données (table {@code revoked_tokens})
 * pour garantir la durabilité entre redémarrages et dans un déploiement multi-instance.
 * Un token blacklisté ne peut plus être utilisé même s'il est cryptographiquement valide.
 * </p>
 * <p>
 * Un nettoyage automatique supprime les entrées expirées toutes les heures
 * pour limiter la croissance de la table en base.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RevokedTokenRepository revokedTokenRepository;

    /**
     * Ajoute un token à la blacklist jusqu'à son expiration naturelle.
     *
     * @param jti         identifiant unique du token (claim {@code jti})
     * @param tokenExpiry instant d'expiration du token
     */
    @Transactional
    public void blacklist(String jti, Instant tokenExpiry) {
        revokedTokenRepository.save(new RevokedToken(jti, tokenExpiry));
    }

    /**
     * Vérifie si un token est blacklisté et toujours dans sa période d'expiration.
     *
     * @param jti identifiant unique du token à vérifier
     * @return {@code true} si le token est blacklisté et n'a pas encore expiré
     */
    @Transactional(readOnly = true)
    public boolean isBlacklisted(String jti) {
        return revokedTokenRepository.findById(jti)
                .map(t -> Instant.now().isBefore(t.getExpiresAt()))
                .orElse(false);
    }

    /**
     * Purge périodique de toutes les entrées expirées de la table.
     * <p>
     * Exécutée toutes les heures (3 600 000 ms).
     * </p>
     */
    @Scheduled(fixedDelay = 3_600_000)
    @Transactional
    public void cleanup() {
        revokedTokenRepository.deleteAllExpiredBefore(Instant.now());
    }
}
