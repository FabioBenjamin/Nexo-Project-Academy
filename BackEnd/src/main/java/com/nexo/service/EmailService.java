package com.nexo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetCode(String toEmail, String username, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Nexo Academy - Código de Recuperação de Senha");
        message.setText(
                "Olá, " + username + "!\n\n" +
                "Recebemos uma solicitação para redefinir a senha da sua conta no Nexo Academy.\n\n" +
                "Seu código de verificação é: " + code + "\n\n" +
                "Este código expira em 15 minutos. Se você não solicitou essa alteração, ignore este e-mail."
        );

        mailSender.send(message);
    }
}