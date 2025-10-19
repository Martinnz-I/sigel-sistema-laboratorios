package com.sigel.SigelApi.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void enviarEmailVerificacion(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String contenidoHTML = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 20px;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: white;
                            padding: 30px;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        }
                        .header {
                            text-align: center;
                            border-bottom: 2px solid #007bff;
                            padding-bottom: 20px;
                            margin-bottom: 20px;
                        }
                        .header h1 {
                            color: #007bff;
                            margin: 0;
                        }
                        .content {
                            color: #333;
                            line-height: 1.6;
                        }
                        .token-box {
                            background-color: #f8f9fa;
                            border-left: 4px solid #007bff;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                            font-family: monospace;
                            word-break: break-all;
                        }
                        .endpoint-box {
                            background-color: #e7f3ff;
                            border-left: 4px solid #007bff;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                            font-family: monospace;
                        }
                        .json-box {
                            background-color: #f8f9fa;
                            border: 1px solid #dee2e6;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                            font-family: monospace;
                            font-size: 12px;
                            overflow-x: auto;
                        }
                        .expiration {
                            background-color: #fff3cd;
                            border-left: 4px solid #ffc107;
                            padding: 10px;
                            margin: 20px 0;
                            border-radius: 4px;
                        }
                        .footer {
                            text-align: center;
                            color: #666;
                            font-size: 12px;
                            border-top: 1px solid #ddd;
                            padding-top: 20px;
                            margin-top: 20px;
                        }
                        .step {
                            margin: 15px 0;
                            padding: 10px;
                            background-color: #f9f9f9;
                            border-radius: 4px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>¡Bienvenido a Sigel!</h1>
                        </div>
                        <div class="content">
                            <p>Hola,</p>
                            <p>Gracias por registrarte en nuestra plataforma. Para completar tu registro, necesitamos verificar tu correo electrónico.</p>
                            
                            <div class="expiration">
                                <strong>⏰ Importante:</strong> Este token vence en 24 horas.
                            </div>
                            
                            <h3>Tu token de verificación:</h3>
                            <div class="token-box">
                                <strong>%s</strong>
                            </div>
                            
                            <h3>Instrucciones para verificar tu email:</h3>
                            
                            <div class="step">
                                <strong>Paso 1:</strong> Copia el token de arriba
                            </div>
                            
                            <div class="step">
                                <strong>Paso 2:</strong> Envía una petición POST al siguiente endpoint:
                                <div class="endpoint-box">
                                    POST http://localhost:8080/api/auth/verificar-email
                                </div>
                            </div>
                            
                            <div class="step">
                                <strong>Paso 3:</strong> Envía el token en el cuerpo de la solicitud con este formato JSON:
                                <div class="json-box">
{
  "token": "%s"
}
                                </div>
                            </div>
                            
                            <p>Si tienes problemas o dudas, contacta a nuestro equipo de soporte.</p>
                        </div>
                        <div class="footer">
                            <p>© 2025 Sigel. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(token, token);

            helper.setTo(email);
            helper.setSubject("Verifica tu email - Sigel");
            helper.setText(contenidoHTML, true);
            helper.setFrom(fromEmail);

            mailSender.send(message);
            logger.info("Email de verificación enviado a: {}", email);

        } catch (MessagingException e) {
            logger.error("Error al enviar email de verificación a: {}", email, e);
            throw new RuntimeException("Error al enviar email de verificación", e);
        }
    }

    public void enviarEmailRecuperacion(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String contenidoHTML = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 20px;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: white;
                            padding: 30px;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        }
                        .header {
                            text-align: center;
                            border-bottom: 2px solid #dc3545;
                            padding-bottom: 20px;
                            margin-bottom: 20px;
                        }
                        .header h1 {
                            color: #dc3545;
                            margin: 0;
                        }
                        .token-box {
                            background-color: #f8f9fa;
                            border-left: 4px solid #dc3545;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                            font-family: monospace;
                            word-break: break-all;
                        }
                        .endpoint-box {
                            background-color: #ffe7e7;
                            border-left: 4px solid #dc3545;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                            font-family: monospace;
                        }
                        .json-box {
                            background-color: #f8f9fa;
                            border: 1px solid #dee2e6;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                            font-family: monospace;
                            font-size: 12px;
                        }
                        .expiration {
                            background-color: #fff3cd;
                            border-left: 4px solid #ffc107;
                            padding: 10px;
                            margin: 20px 0;
                            border-radius: 4px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Recupera tu contraseña</h1>
                        </div>
                        <div class="content">
                            <p>Recibimos una solicitud para recuperar tu contraseña.</p>
                            
                            <div class="expiration">
                                <strong>⏰ Importante:</strong> Este token vence en 1 hora.
                            </div>
                            
                            <h3>Tu token de recuperación:</h3>
                            <div class="token-box">
                                <strong>%s</strong>
                            </div>
                            
                            <h3>Para recuperar tu contraseña:</h3>
                            <p>Envía una petición POST al siguiente endpoint:</p>
                            <div class="endpoint-box">
                                POST http://localhost:8080/api/auth/recuperar-password
                            </div>
                            
                            <p>Con el siguiente formato JSON:</p>
                            <div class="json-box">
{
  "token": "%s",
  "nuevaContrasena": "tu-nueva-contraseña"
}
                            </div>
                            
                            <p>Si no solicitaste recuperar tu contraseña, puedes ignorar este email de forma segura.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(token, token);

            helper.setTo(email);
            helper.setSubject("Recupera tu contraseña - Sigel");
            helper.setText(contenidoHTML, true);
            helper.setFrom(fromEmail);

            mailSender.send(message);
            logger.info("Email de recuperación enviado a: {}", email);

        } catch (MessagingException e) {
            logger.error("Error al enviar email de recuperación", e);
            throw new RuntimeException("Error al enviar email", e);
        }
    }
}