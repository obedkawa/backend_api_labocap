package com.labo.anapath.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de la documentation OpenAPI (Swagger UI).
 * <p>
 * Déclare les métadonnées de l'API (titre, description, version, contact) et
 * configure le schéma de sécurité JWT Bearer de façon globale, ce qui permet
 * de tester les endpoints protégés directement depuis Swagger UI.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /** Nom du schéma de sécurité référencé dans la spécification OpenAPI. */
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * Construit le bean {@link OpenAPI} avec les informations générales de l'API
     * et la configuration du schéma d'authentification Bearer JWT.
     *
     * @return la configuration OpenAPI prête à être exposée par SpringDoc
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LIS Labo-Anapath API")
                        .description("API REST de gestion de laboratoire d'anatomopathologie")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Labo Anapath")
                                .email("support@labo-anapath.com")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Fournir le token JWT Bearer")));
    }
}
