package com.labo.anapath.auth;

import com.labo.anapath.common.exception.InvalidCodeException;
import com.labo.anapath.common.exception.UnauthorizedException;
import com.labo.anapath.common.security.CustomUserDetailsService;
import com.labo.anapath.common.security.JwtProperties;
import com.labo.anapath.common.security.JwtTokenProvider;
import com.labo.anapath.common.security.TokenBlacklistService;
import com.labo.anapath.common.security.UserPrincipal;
import com.labo.anapath.user.User;
import com.labo.anapath.user.UserMapper;
import com.labo.anapath.user.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Implémentation du service d'authentification JWT avec support 2FA Google Authenticator.
 * <p>
 * Gère l'intégralité du flux d'authentification :
 * <ol>
 *   <li>Login par identifiants via Spring Security {@link AuthenticationManager}.</li>
 *   <li>Détection de la 2FA activée → émission d'un token temporaire de challenge (5 min).</li>
 *   <li>Validation du code TOTP → émission des tokens définitifs.</li>
 *   <li>Rafraîchissement des tokens via le refresh token.</li>
 *   <li>Logout avec blacklisting du token courant.</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GoogleAuthenticator googleAuthenticator;

    /**
     * {@inheritDoc}
     * <p>
     * Délègue la vérification des identifiants à Spring Security. Si la 2FA est
     * activée, retourne un token temporaire. Sinon, met à jour {@code isConnect}
     * et {@code lastLoginDevice} en base avant de retourner les tokens définitifs.
     * </p>
     *
     * @throws UnauthorizedException si le compte est désactivé ou si les identifiants sont incorrects
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

            // If 2FA is enabled, return a short-lived temp token instead of full JWT
            if (user.isTwoFactorEnabled()) {
                String tempToken = jwtTokenProvider.generateTempToken(userPrincipal.getId());
                log.info("2FA challenge requis pour: {}", request.getEmail());
                return LoginResponse.requires2fa(tempToken);
            }

            String accessToken = jwtTokenProvider.generateToken(userPrincipal);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal.getId());
            long expiresIn = jwtProperties.getExpirationMs() / 1000;

            user.setConnect(true);
            user.setLastLoginDevice(getUserAgentHash());
            userRepository.save(user);

            log.info("Connexion réussie pour: {}", request.getEmail());
            return new LoginResponse(accessToken, refreshToken, expiresIn, userMapper.toResponseDto(user));
        } catch (DisabledException ex) {
            log.warn("Compte désactivé pour: {}", request.getEmail());
            throw new UnauthorizedException("Compte désactivé.");
        } catch (BadCredentialsException ex) {
            log.warn("Échec de connexion pour: {}", request.getEmail());
            throw new UnauthorizedException("Email ou mot de passe incorrect.");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Valide la signature, le type ({@code "refresh"}) et l'absence de blacklisting
     * avant d'émettre un nouveau couple access+refresh token. Vérifie également
     * que le compte est toujours actif.
     * </p>
     *
     * @throws UnauthorizedException si le refresh token est invalide, révoqué ou si le compte est désactivé
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse refresh(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Refresh token invalide ou expiré.");
        }
        String tokenType = jwtTokenProvider.extractType(token);
        if (!"refresh".equals(tokenType)) {
            throw new UnauthorizedException("Token fourni n'est pas un refresh token.");
        }
        String jti = jwtTokenProvider.extractJti(token);
        if (tokenBlacklistService.isBlacklisted(jti)) {
            throw new UnauthorizedException("Ce token a été révoqué.");
        }
        UUID userId = jwtTokenProvider.extractUserId(token);
        UserPrincipal userPrincipal = (UserPrincipal) customUserDetailsService.loadUserById(userId);
        String newAccessToken = jwtTokenProvider.generateToken(userPrincipal);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        long expiresIn = jwtProperties.getExpirationMs() / 1000;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));
        if (!user.isActive()) {
            throw new UnauthorizedException("Compte désactivé.");
        }

        return new LoginResponse(newAccessToken, newRefreshToken, expiresIn, userMapper.toResponseDto(user));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Le JTI du token est blacklisté jusqu'à son expiration naturelle. L'état
     * {@code isConnect} de l'utilisateur est mis à {@code false} en base. Si le
     * token est absent ou invalide, l'opération est ignorée sans erreur.
     * </p>
     */
    @Override
    public void logout(String token) {
        if (!StringUtils.hasText(token)) {
            return;
        }
        try {
            String jti = jwtTokenProvider.extractJti(token);
            if (StringUtils.hasText(jti)) {
                tokenBlacklistService.blacklist(jti, jwtTokenProvider.extractExpiry(token));
            }
            // Mise à jour du statut de connexion en base
            UUID userId = jwtTokenProvider.extractUserId(token);
            userRepository.findById(userId).ifPresent(user -> {
                user.setConnect(false);
                user.setTwoFactorEnabled(false);
                userRepository.save(user);
            });
            log.info("Déconnexion — token blacklisté (jti={})", jti);
        } catch (Exception ex) {
            log.warn("Logout — erreur lors du traitement du token: {}", ex.getMessage());
        }
    }

    /**
     * Calcule le hash SHA-256 de l'en-tête {@code User-Agent} de la requête courante.
     * <p>
     * Utilisé pour stocker une empreinte du dispositif de connexion sans conserver
     * la valeur brute de l'User-Agent en base.
     * </p>
     *
     * @return hash hexadécimal SHA-256 du User-Agent, ou {@code null} si non disponible
     */
    private String getUserAgentHash() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            String userAgent = attrs.getRequest().getHeader("User-Agent");
            if (!StringUtils.hasText(userAgent)) return null;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(userAgent.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            log.warn("Impossible de calculer le hash User-Agent: {}", e.getMessage());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Vérifie la validité et le type du token temporaire, puis valide le code TOTP
     * via Google Authenticator. En cas de succès, émet les tokens définitifs et
     * met à jour l'état de connexion en base.
     * </p>
     *
     * @throws UnauthorizedException si le token temporaire est invalide ou si l'utilisateur est introuvable
     * @throws InvalidCodeException  si le code TOTP est incorrect ou non numérique
     */
    @Override
    @Transactional
    public LoginResponse challenge(TwoFactorVerifyRequest request) {
        String tempToken = request.getTempToken();
        if (!StringUtils.hasText(tempToken) || !jwtTokenProvider.validateToken(tempToken)) {
            throw new UnauthorizedException("Token temporaire invalide ou expiré.");
        }
        String tokenType = jwtTokenProvider.extractType(tempToken);
        if (!"2fa-challenge".equals(tokenType)) {
            throw new UnauthorizedException("Token temporaire invalide.");
        }

        UUID userId = jwtTokenProvider.extractUserId(tempToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Code TOTP invalide"));

        if (user.getTwoFactorSecret() == null) {
            throw new UnauthorizedException("Code TOTP invalide");
        }

        int code;
        try {
            code = Integer.parseInt(request.getCode().trim());
        } catch (NumberFormatException e) {
            throw new InvalidCodeException("Le code TOTP doit être numérique");
        }

        if (!googleAuthenticator.authorize(user.getTwoFactorSecret(), code)) {
            throw new InvalidCodeException("Code TOTP invalide");
        }

        UserPrincipal userPrincipal = (UserPrincipal) customUserDetailsService.loadUserById(userId);
        String accessToken = jwtTokenProvider.generateToken(userPrincipal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);
        long expiresIn = jwtProperties.getExpirationMs() / 1000;

        user.setConnect(true);
        user.setLastLoginDevice(getUserAgentHash());
        userRepository.save(user);

        log.info("2FA challenge réussi pour userId={}", userId);
        return new LoginResponse(accessToken, refreshToken, expiresIn, userMapper.toResponseDto(user));
    }
}
