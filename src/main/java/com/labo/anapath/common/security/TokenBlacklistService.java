package com.labo.anapath.common.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service de blacklist des tokens JWT révoqués après déconnexion.
 * <p>
 * Stocke en mémoire (via {@link ConcurrentHashMap}) les identifiants uniques JTI
 * des tokens invalidés, associés à leur date d'expiration. Un token blacklisté
 * ne peut plus être utilisé même s'il est cryptographiquement valide.
 * </p>
 * <p>
 * Un nettoyage automatique supprime les entrées expirées toutes les heures
 * pour limiter la croissance de la map en mémoire.
 * </p>
 * <p>
 * Limitation : cette implémentation est en mémoire uniquement — les tokens blacklistés
 * sont perdus au redémarrage du serveur. Une implémentation Redis serait nécessaire
 * dans un déploiement multi-instance.
 * </p>
 */
@Service
public class TokenBlacklistService {

    /** Map JTI → date d'expiration du token révoqué. Thread-safe par conception. */
    private final ConcurrentHashMap<String, Instant> blacklist = new ConcurrentHashMap<>();

    /**
     * Ajoute un token à la blacklist jusqu'à son expiration naturelle.
     *
     * @param jti         identifiant unique du token (claim {@code jti})
     * @param tokenExpiry instant d'expiration du token (utilisé pour le nettoyage automatique)
     */
    public void blacklist(String jti, Instant tokenExpiry) {
        blacklist.put(jti, tokenExpiry);
    }

    /**
     * Vérifie si un token est blacklisté et toujours dans sa période d'expiration.
     * <p>
     * Si le token est trouvé mais a expiré, il est retiré de la blacklist immédiatement
     * (nettoyage opportuniste).
     * </p>
     *
     * @param jti identifiant unique du token à vérifier
     * @return {@code true} si le token est blacklisté et n'a pas encore expiré
     */
    public boolean isBlacklisted(String jti) {
        Instant expiry = blacklist.get(jti);
        if (expiry == null) return false;
        if (Instant.now().isAfter(expiry)) {
            // Nettoyage opportuniste : le token a expiré naturellement, inutile de le garder
            blacklist.remove(jti);
            return false;
        }
        return true;
    }

    /**
     * Purge périodique de toutes les entrées expirées de la blacklist.
     * <p>
     * Exécutée toutes les heures (3 600 000 ms) pour éviter une croissance illimitée
     * de la map en mémoire.
     * </p>
     */
    @Scheduled(fixedDelay = 3_600_000)
    public void cleanup() {
        Instant now = Instant.now();
        blacklist.entrySet().removeIf(e -> now.isAfter(e.getValue()));
    }
}
