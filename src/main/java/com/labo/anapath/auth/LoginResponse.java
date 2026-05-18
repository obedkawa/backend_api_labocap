package com.labo.anapath.auth;

import com.labo.anapath.user.UserResponseDto;

/**
 * DTO de réponse du flux d'authentification.
 * <p>
 * Représente deux états distincts selon que la 2FA est activée ou non :
 * <ul>
 *   <li><b>Connexion directe (2FA désactivée)</b> : {@code accessToken}, {@code refreshToken},
 *       {@code tokenType} ("Bearer"), {@code expiresIn} et {@code user} sont renseignés ;
 *       {@code requires2fa} et {@code tempToken} sont {@code null}.</li>
 *   <li><b>Challenge 2FA requis</b> : seuls {@code requires2fa = true} et {@code tempToken}
 *       sont renseignés ; les autres champs sont {@code null}.</li>
 * </ul>
 * </p>
 *
 * @param accessToken  token d'accès JWT (null si challenge 2FA)
 * @param refreshToken token de rafraîchissement JWT (null si challenge 2FA)
 * @param tokenType    type de token, toujours "Bearer" (null si challenge 2FA)
 * @param expiresIn    durée de validité du token d'accès en secondes (null si challenge 2FA)
 * @param user         informations de l'utilisateur connecté (null si challenge 2FA)
 * @param requires2fa  {@code true} si un challenge TOTP est requis pour finaliser la connexion
 * @param tempToken    token temporaire de challenge 2FA valide 5 min (null si pas de 2FA)
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserResponseDto user,
        Boolean requires2fa,
        String tempToken
) {
    /**
     * Constructeur de commodité pour une connexion réussie sans 2FA.
     *
     * @param accessToken  token d'accès JWT
     * @param refreshToken token de rafraîchissement JWT
     * @param expiresIn    durée de validité du token d'accès en secondes
     * @param user         informations de l'utilisateur connecté
     */
    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, UserResponseDto user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user, null, null);
    }

    /**
     * Fabrique une réponse indiquant qu'un challenge 2FA est requis.
     *
     * @param tempToken token temporaire à présenter sur {@code /api/v1/auth/2fa/challenge}
     * @return réponse avec {@code requires2fa = true} et uniquement le token temporaire
     */
    public static LoginResponse requires2fa(String tempToken) {
        return new LoginResponse(null, null, null, null, null, true, tempToken);
    }
}
