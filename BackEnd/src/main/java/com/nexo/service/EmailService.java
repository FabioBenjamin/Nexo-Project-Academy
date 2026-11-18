package com.nexo.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetCode(String toEmail, String username, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(fromEmail, "Nexo Academy");
            helper.setTo(toEmail);
            helper.setSubject("Seu código de recuperação do Nexo Academy é " + code);

            String plainText = String.format(
                    "Olá, %s!\n\n" +
                            "Insira o código nos próximos 15 minutos para redefinir a senha da sua conta no Nexo Academy:\n\n" +
                            "%s\n\n" +
                            "Se você não solicitou essa alteração, ignore este e-mail.",
                    username, code
            );

            String htmlContent = buildPasswordResetHtml(username, code);

            helper.setText(plainText, htmlContent);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar e-mail de recuperação para " + toEmail, e);
        }
    }

    private String buildPasswordResetHtml(String username, String code) {
        String safeUsername = escapeHtml(username);
        String safeCode = escapeHtml(code);

        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; background-color: #F4F5F7; font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="background-color: #F4F5F7; padding: 40px 16px;">
                    <tr>
                        <td align="center">
                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="max-width: 520px; background-color: #FFFFFF; border-radius: 16px; border: 1px solid #E5E7EB; overflow: hidden;">
                                
                                <!-- Logo / Cabeçalho -->
                                <tr>
                                    <td style="padding: 32px 36px 8px 36px; text-align: left;">
                                        <span style="font-size: 22px; font-weight: 800; color: #7C3AED; letter-spacing: -0.3px;">
                                            Nexo Academy
                                        </span>
                                    </td>
                                </tr>

                                <!-- Título Principal -->
                                <tr>
                                    <td style="padding: 12px 36px 8px 36px; text-align: left;">
                                        <h1 style="margin: 0; font-size: 24px; line-height: 1.3; font-weight: 800; color: #1A1F2E;">
                                            Redefina a senha da sua conta no Nexo Academy
                                        </h1>
                                    </td>
                                </tr>

                                <!-- Saudação e Instrução -->
                                <tr>
                                    <td style="padding: 8px 36px 24px 36px; text-align: left;">
                                        <p style="margin: 0; font-size: 15px; line-height: 1.6; color: #4B5563;">
                                            Olá, <strong>%s</strong>! Insira o código abaixo nos próximos <strong>15 minutos</strong> para redefinir a senha da sua conta.
                                        </p>
                                    </td>
                                </tr>

                                <!-- Bloco de Código em Destaque (estilo limpo para seleção com 1 toque) -->
                                <tr>
                                    <td style="padding: 8px 36px 28px 36px; text-align: left;">
                                        <div style="background-color: #FAF8FF; border: 1.5px solid #DDD6FE; border-radius: 12px; padding: 20px 24px; text-align: center;">
                                            <span style="font-family: 'Courier New', Courier, monospace; font-size: 36px; font-weight: 800; letter-spacing: 8px; color: #1A1F2E; display: inline-block; padding-left: 8px;">
                                                %s
                                            </span>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Divisória -->
                                <tr>
                                    <td style="padding: 0 36px;">
                                        <hr style="border: none; border-top: 1px solid #E5E7EB; margin: 0;">
                                    </td>
                                </tr>

                                <!-- Aviso Transacional no Rodapé -->
                                <tr>
                                    <td style="padding: 20px 36px 32px 36px; text-align: left;">
                                        <p style="margin: 0 0 12px 0; font-size: 12px; line-height: 1.6; color: #6B7280;">
                                            Você recebeu esse e-mail porque tem uma conta no Nexo Academy e foi solicitada uma redefinição de senha. Se você não solicitou essa alteração, ignore este e-mail com segurança.
                                        </p>
                                        <p style="margin: 0; font-size: 12px; font-weight: 700; color: #9CA3AF;">
                                            Nexo Academy • Aprenda no seu ritmo
                                        </p>
                                    </td>
                                </tr>

                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(safeUsername, safeCode);
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}