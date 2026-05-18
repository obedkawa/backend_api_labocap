package com.labo.anapath.auth;

/**
 * DTO de réponse pour l'initialisation de la 2FA.
 * <p>
 * Retourné par {@code POST /api/v1/auth/2fa/setup}. Le client doit afficher
 * le QR code à l'utilisateur pour qu'il le scanne avec Google Authenticator,
 * ou lui permettre de saisir manuellement le secret.
 * </p>
 *
 * @param secret       secret TOTP en base32, à conserver côté client pour la saisie manuelle
 * @param qrCodeBase64 image PNG du QR code encodée en base64, à afficher directement dans le frontend
 */
public record TwoFaSetupResponse(String secret, String qrCodeBase64) {
}
