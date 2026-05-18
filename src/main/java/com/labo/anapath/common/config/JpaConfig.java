package com.labo.anapath.common.config;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration JPA et planification des tâches.
 * <p>
 * Active les repositories JPA sur l'ensemble du package {@code com.labo.anapath}
 * et active la planification Spring ({@code @Scheduled}) pour les tâches périodiques
 * telles que le nettoyage de la blacklist de tokens.
 * </p>
 * <p>
 * Déclare également le bean {@link GoogleAuthenticator} partagé pour la génération
 * et la vérification des codes TOTP (2FA).
 * </p>
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.labo.anapath")
@EnableScheduling
public class JpaConfig {

    /**
     * Crée l'instance partagée de {@link GoogleAuthenticator} utilisée pour
     * la génération de secrets TOTP et la vérification des codes 2FA.
     *
     * @return une nouvelle instance de {@link GoogleAuthenticator}
     */
    @Bean
    public GoogleAuthenticator googleAuthenticator() {
        return new GoogleAuthenticator();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
