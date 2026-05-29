package com.labo.anapath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Point d'entrée principal de l'application LIS Labo-Anapath.
 * <p>
 * Active l'audit JPA automatique via {@code AuditorAwareImpl} pour alimenter
 * les champs {@code createdBy} et {@code updatedBy} sur toutes les entités
 * héritant de {@link com.labo.anapath.common.audit.AuditableEntity}.
 * </p>
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableScheduling
@EnableAsync
public class AnaPathApplication {

    /**
     * Démarre le contexte Spring Boot.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SpringApplication.run(AnaPathApplication.class, args);
    }
}
