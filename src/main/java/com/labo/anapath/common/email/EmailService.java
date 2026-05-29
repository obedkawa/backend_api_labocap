package com.labo.anapath.common.email;

public interface EmailService {

    /**
     * Envoie le code OTP 2FA par email de manière asynchrone.
     *
     * @param to        adresse email du destinataire
     * @param firstname prénom pour personnaliser le message
     * @param otp       code OTP à 6 chiffres en clair (affiché dans l'email)
     */
    void sendOtp(String to, String firstname, String otp);
}
