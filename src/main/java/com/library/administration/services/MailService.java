package com.library.administration.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            
        } catch (MailException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo", e);
        }

    }

    public void sendPasswordResetEmail(String email, String recoveryToken) {
        String resetLink = "http://localhost:8080/api/v1/auth/reset-password?token=" + recoveryToken;
        String subject = "Recuperación de contraseña";
        String body = "Hola,\n\n"
                + "Has solicitado restablecer tu contraseña. Por favor, haz clic en el siguiente enlace para continuar:\n"
                + resetLink + "\n\n"
                + "Si no has solicitado esto, ignora este mensaje.\n\n"
                + "Gracias.";

        sendMail(email, subject, body);
    }
}
