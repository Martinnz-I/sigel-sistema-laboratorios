package com.sigel.SigelApi.service;

import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final ResourceLoader resourceLoader;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl;

    public void enviarEmailVerificacion(String email, String token) {
        String asunto = "Verifica tu email - SGLab";
        String contenidoHTML = construirEmailVerificacion(token);
        enviarEmail(email, asunto, contenidoHTML);
    }

    public void enviarEmailRecuperacion(String email, String token) {
        String asunto = "Recupera tu contraseña - SGLab";
        String contenidoHTML = construirEmailRecuperacion(token);
        enviarEmail(email, asunto, contenidoHTML);
    }

    private void enviarEmail(String destinatario, String asunto, String contenidoHTML) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true);
            helper.setFrom(fromEmail);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar email a: " + destinatario, e);
        }
    }

    private String construirEmailVerificacion(String token) {
        String urlVerificacion = frontendUrl + "/verificar-email?token=" + token;

        String template = cargarPlantilla("classpath:templates/verificacion-email.html");

        return template
                .replace("{{urlVerificacion}}", urlVerificacion)
                .replace("{{token}}", token)
                .replace("{{backendUrl}}", backendUrl);
    }

    private String construirEmailRecuperacion(String token) {
        String urlRecuperacion = frontendUrl + "/restablecer-password?token=" + token;

        String template = cargarPlantilla("classpath:templates/recuperacion-password.html");

        return template
                .replace("{{urlRecuperacion}}", urlRecuperacion)
                .replace("{{token}}", token)
                .replace("{{backendUrl}}", backendUrl);
    }

    private String cargarPlantilla(String ruta) {
        try {
            Resource resource = resourceLoader.getResource(ruta);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Error al cargar la plantilla de email");
        }
    }
}