package com.labo.anapath.common.security;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Propriétés de configuration JWT lues depuis {@code application.yml} sous le préfixe {@code app.jwt}.
 * <p>
 * Exemple de configuration :
 * <pre>{@code
 * app:
 *   jwt:
 *     secret: "<clé-hmac-256-bits>"
 *     expiration-ms: 86400000      # 24 heures
 *     refresh-expiration-ms: 604800000  # 7 jours
 * }</pre>
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /** Clé secrète HMAC-SHA utilisée pour signer et vérifier les tokens JWT. */
    private String secret;

    /** Durée de validité du token d'accès en millisecondes (défaut : 86 400 000 ms = 24 h). */
    private long expirationMs;

    /** Durée de validité du token de rafraîchissement en millisecondes (défaut : 604 800 000 ms = 7 jours). */
    private long refreshExpirationMs;

    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException(
                "La variable d'environnement JWT_SECRET est obligatoire. " +
                "Générez une clé forte avec: openssl rand -base64 32");
        }
        if (secret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                "JWT_SECRET doit faire au moins 256 bits (32 octets). " +
                "Générez une clé forte avec: openssl rand -base64 32");
        }
    }
}
