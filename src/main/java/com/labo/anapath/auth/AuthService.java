package com.labo.anapath.auth;

/**
 * Contrat du service d'authentification JWT.
 * <p>
 * Définit les opérations du flux d'authentification complet, incluant la gestion
 * du challenge 2FA (Google Authenticator TOTP).
 * </p>
 */
public interface AuthService {

    /**
     * Authentifie un utilisateur avec ses identifiants.
     * <p>
     * Si la 2FA est activée sur le compte, retourne un token temporaire de challenge
     * ({@code requires2fa = true}) valide 5 minutes. Sinon, retourne les tokens
     * d'accès et de rafraîchissement définitifs.
     * </p>
     *
     * @param request identifiants de connexion (email + mot de passe)
     * @return réponse avec les tokens ou le challenge 2FA
     */
    LoginResponse login(LoginRequest request);

    /**
     * Renouvelle le token d'accès à partir d'un refresh token valide et non révoqué.
     *
     * @param request requête contenant le refresh token
     * @return nouveaux tokens d'accès et de rafraîchissement
     */
    LoginResponse refresh(RefreshTokenRequest request);

    /**
     * Révoque le token JWT fourni en l'ajoutant à la blacklist.
     * <p>
     * Met également à jour l'état de connexion de l'utilisateur en base ({@code is_connect = false}).
     * Si le token est absent ou invalide, l'opération est ignorée silencieusement.
     * </p>
     *
     * @param token token JWT à révoquer (sans le préfixe "Bearer ")
     */
    void logout(String token);

    /**
     * Valide le code TOTP soumis dans le cadre du challenge 2FA et émet les tokens définitifs.
     *
     * @param request requête contenant le token temporaire de challenge et le code TOTP à 6 chiffres
     * @return tokens d'accès et de rafraîchissement définitifs
     */
    LoginResponse challenge(TwoFactorVerifyRequest request);
}
