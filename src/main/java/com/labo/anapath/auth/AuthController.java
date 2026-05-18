package com.labo.anapath.auth;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST gérant l'authentification et les opérations 2FA.
 * <p>
 * Expose les endpoints publics et protégés du module auth sous {@code /api/v1/auth}.
 * Le flux d'authentification complet avec 2FA est le suivant :
 * <ol>
 *   <li>{@code POST /login} → retourne un access+refresh token si la 2FA est désactivée,
 *       ou un token temporaire ({@code requires2fa=true}) si elle est activée.</li>
 *   <li>{@code POST /2fa/challenge} → soumet le code TOTP avec le token temporaire
 *       et reçoit les tokens définitifs en retour.</li>
 * </ol>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TwoFaService twoFaService;

    /**
     * Authentifie un utilisateur avec son email et son mot de passe.
     * <p>
     * Si la 2FA est activée sur le compte, retourne un token temporaire de challenge
     * ({@code requires2fa = true}) ; sinon retourne directement les tokens d'accès et de rafraîchissement.
     * </p>
     *
     * @param request corps de la requête contenant email et mot de passe
     * @return réponse contenant les tokens ou le challenge 2FA
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Connexion réussie", response));
    }

    /**
     * Rafraîchit le token d'accès à partir d'un refresh token valide.
     *
     * @param request corps contenant le refresh token
     * @return nouveaux tokens d'accès et de rafraîchissement
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("Token rafraîchi", response));
    }

    /**
     * Déconnecte l'utilisateur en blacklistant son token JWT courant.
     *
     * @param httpRequest requête HTTP pour extraire le token Bearer
     * @param principal   utilisateur authentifié (non utilisé directement, présent pour la sécurité Spring)
     * @return réponse de confirmation de déconnexion
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal principal) {
        authService.logout(extractBearerToken(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Déconnexion réussie", null));
    }

    /**
     * Initialise la configuration 2FA pour l'utilisateur connecté.
     * <p>
     * Génère un secret TOTP, le persiste et retourne le QR code (base64 PNG)
     * à scanner avec Google Authenticator. La 2FA n'est pas encore activée
     * tant que {@code /2fa/verify} n'est pas appelé avec succès.
     * </p>
     *
     * @param principal utilisateur authentifié dont on configure la 2FA
     * @return secret TOTP et QR code base64 pour la configuration de l'application TOTP
     */
    @PostMapping("/2fa/setup")
    public ResponseEntity<ApiResponse<TwoFaSetupResponse>> setup2fa(
            @AuthenticationPrincipal UserPrincipal principal) {
        TwoFaSetupResponse response = twoFaService.setup(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("QR code généré", response));
    }

    /**
     * Vérifie le premier code TOTP et active la 2FA sur le compte.
     * <p>
     * Doit être appelé après {@code /2fa/setup} pour confirmer que l'utilisateur
     * a bien scanné le QR code et que son application TOTP fonctionne correctement.
     * </p>
     *
     * @param request   code TOTP à 6 chiffres saisi par l'utilisateur
     * @param principal utilisateur authentifié
     * @return confirmation d'activation
     */
    @PostMapping("/2fa/verify")
    public ResponseEntity<ApiResponse<Void>> verify2fa(
            @Valid @RequestBody TwoFaCodeRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        twoFaService.verifyAndEnable(principal.getId(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success("2FA activée avec succès", null));
    }

    /**
     * Désactive la 2FA sur le compte après vérification du code TOTP courant.
     *
     * @param request   code TOTP à 6 chiffres pour confirmer la désactivation
     * @param principal utilisateur authentifié
     * @return confirmation de désactivation
     */
    @PostMapping("/2fa/disable")
    public ResponseEntity<ApiResponse<Void>> disable2fa(
            @Valid @RequestBody TwoFaCodeRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        twoFaService.disable(principal.getId(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success("2FA désactivée", null));
    }

    /**
     * Valide le code TOTP dans le cadre du flux de challenge 2FA.
     * <p>
     * Endpoint public (sans authentification complète). L'utilisateur présente
     * son token temporaire ({@code 2fa-challenge}) et son code TOTP ; en cas de
     * succès, il reçoit les tokens d'accès et de rafraîchissement définitifs.
     * </p>
     *
     * @param request corps contenant le token temporaire et le code TOTP
     * @return tokens d'accès et de rafraîchissement définitifs
     */
    @PostMapping("/2fa/challenge")
    public ResponseEntity<ApiResponse<LoginResponse>> challenge2fa(
            @Valid @RequestBody TwoFactorVerifyRequest request) {
        LoginResponse response = authService.challenge(request);
        return ResponseEntity.ok(ApiResponse.success("Authentification 2FA réussie", response));
    }

    /**
     * Extrait le token JWT brut depuis l'en-tête {@code Authorization: Bearer <token>}.
     *
     * @param request requête HTTP entrante
     * @return le token sans le préfixe "Bearer ", ou {@code null} si l'en-tête est absent
     */
    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
