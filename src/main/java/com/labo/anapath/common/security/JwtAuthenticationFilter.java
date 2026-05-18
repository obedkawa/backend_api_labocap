package com.labo.anapath.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtre HTTP chargé de valider le token JWT et d'alimenter le contexte de sécurité Spring.
 * <p>
 * Exécuté une seule fois par requête (extends {@link OncePerRequestFilter}). Pour chaque
 * requête entrante, il :
 * <ol>
 *   <li>Extrait le token Bearer depuis l'en-tête {@code Authorization}.</li>
 *   <li>Valide la signature et l'expiration via {@link JwtTokenProvider}.</li>
 *   <li>Rejette explicitement les tokens de type {@code 2fa-challenge} et {@code refresh}
 *       qui ne doivent pas accorder d'accès complet.</li>
 *   <li>Vérifie que le JTI n'est pas blacklisté (token révoqué après logout).</li>
 *   <li>Charge l'utilisateur et positionne l'authentification dans le {@link SecurityContextHolder}.</li>
 * </ol>
 * En cas d'erreur, la requête continue sans authentification — Spring Security se charge
 * ensuite du rejet si la ressource est protégée.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Logique principale du filtre : validation du JWT et alimentation du contexte de sécurité.
     *
     * @param request     requête HTTP entrante
     * @param response    réponse HTTP
     * @param filterChain chaîne de filtres à poursuivre
     * @throws ServletException en cas d'erreur servlet
     * @throws IOException      en cas d'erreur d'entrée/sortie
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                // Reject 2FA challenge tokens — they must not authenticate full access
                String tokenType = jwtTokenProvider.extractType(jwt);
                if ("2fa-challenge".equals(tokenType)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                            "Token de challenge 2FA non autorisé sur cet endpoint");
                    return;
                }
                if ("refresh".equals(tokenType)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                            "Token de rafraîchissement non autorisé pour l'authentification");
                    return;
                }
                String jti = jwtTokenProvider.extractJti(jwt);
                if (jti != null && tokenBlacklistService.isBlacklisted(jti)) {
                    // Token révoqué après logout : on laisse passer sans authentification
                    filterChain.doFilter(request, response);
                    return;
                }
                UUID userId = jwtTokenProvider.extractUserId(jwt);
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context: {}", ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT brut depuis l'en-tête {@code Authorization: Bearer <token>}.
     *
     * @param request requête HTTP
     * @return le token JWT sans le préfixe "Bearer ", ou {@code null} si l'en-tête est absent ou malformé
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
