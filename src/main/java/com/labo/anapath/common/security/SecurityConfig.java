package com.labo.anapath.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration principale de la sécurité Spring Security.
 * <p>
 * Déclare une architecture stateless JWT sans session HTTP ni CSRF.
 * Le contrôle d'accès fin (RBAC par slugs de permissions) est délégué à
 * {@code @PreAuthorize} au niveau des méthodes ({@code @EnableMethodSecurity}).
 * </p>
 *
 * <p><b>Endpoints publics (sans authentification) :</b></p>
 * <ul>
 *   <li>{@code POST /api/v1/auth/login} — connexion initiale</li>
 *   <li>{@code POST /api/v1/auth/refresh} — rafraîchissement du token</li>
 *   <li>{@code POST /api/v1/auth/2fa/challenge} — validation du code TOTP</li>
 *   <li>{@code GET /actuator/health} — sonde de santé</li>
 *   <li>{@code /v3/api-docs/**} et {@code /swagger-ui/**} — documentation OpenAPI</li>
 * </ul>
 * <p>Tous les autres endpoints requièrent un token d'accès JWT valide.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Configure la chaîne de filtres de sécurité HTTP.
     * <p>
     * Désactive CSRF (API stateless), impose une politique de session STATELESS
     * et insère le filtre JWT avant le filtre d'authentification standard de Spring.
     * </p>
     *
     * @param http constructeur de configuration HTTP de Spring Security
     * @return la {@link SecurityFilterChain} construite
     * @throws Exception si la configuration échoue
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/refresh").permitAll()
                        .requestMatchers("/api/v1/auth/2fa/challenge").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/files/**").permitAll()  // Fichiers locaux — protégés par obscurité UUID
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Déclare le provider d'authentification DAO utilisant BCrypt pour la vérification
     * du mot de passe et {@link CustomUserDetailsService} pour le chargement des utilisateurs.
     *
     * @return le {@link DaoAuthenticationProvider} configuré
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Déclare l'encodeur de mots de passe BCrypt utilisé pour le hashage lors
     * de la création de compte et pour la vérification lors de l'authentification.
     *
     * @return un {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expose l'{@link AuthenticationManager} du contexte Spring comme bean injectable,
     * nécessaire pour déclencher l'authentification programmatique dans {@link com.labo.anapath.auth.AuthServiceImpl}.
     *
     * @param config configuration d'authentification Spring
     * @return l'{@link AuthenticationManager} actif
     * @throws Exception si la récupération du manager échoue
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
