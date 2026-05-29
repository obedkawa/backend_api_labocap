package com.labo.anapath.common.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:noreply@labo-anapath.bj}")
    private String fromEmail;

    @Value("${app.mail.from-name:Labo AnaPath}")
    private String fromName;

    @Async
    @Override
    public void sendOtp(String to, String firstname, String otp) {
        try {
            Context context = new Context();
            context.setVariable("firstname", firstname);
            context.setVariable("otp", otp);
            context.setVariable("expiryMinutes", 10);

            String htmlContent = templateEngine.process("email/otp-2fa", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject("Votre code de connexion — Labo AnaPath");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email OTP envoyé à: {}", maskEmail(to));
        } catch (Exception e) {
            log.error("Échec d'envoi de l'email OTP à {}: {}", maskEmail(to), e.getMessage());
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@", 2);
        String user = parts[0];
        String masked = user.length() > 2
                ? user.charAt(0) + "***" + user.charAt(user.length() - 1)
                : "***";
        return masked + "@" + parts[1];
    }
}
