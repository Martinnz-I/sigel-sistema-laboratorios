package com.sigel.SigelApi.asyncs;

import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.service.EmailService;
import com.sigel.SigelApi.service.StorageService;
import com.sigel.SigelApi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroAsyncService {

    private final StorageService storageService;
    private final EmailService emailService;
    private final UsuarioService usuarioService;

    @Async("taskExecutor")
    public void procesarPostRegistro(Long usuarioId, MultipartFile imagen,
                                     String email, String token) {
        try {
            CompletableFuture<String> imagenFuture = CompletableFuture.supplyAsync(() -> {
                if (imagen != null && !imagen.isEmpty()) {
                    return storageService.subirImagen(imagen);
                }
                return null;
            });

            CompletableFuture<Void> emailFuture = CompletableFuture.runAsync(() -> {
                emailService.enviarEmailVerificacion(email, token);
            });

            CompletableFuture.allOf(imagenFuture, emailFuture).join();

            String fotoUrl = imagenFuture.join();
            if (fotoUrl != null) {
                Usuario usuario = usuarioService.buscarPorId(usuarioId,
                        new RuntimeException("Usuario no encontrado"));
                usuario.setFotoPerfilUrl(fotoUrl);
                usuarioService.guardar(usuario);
            }
        } catch (Exception e) {
            log.error("❌ [ASYNC] Error: {}", e.getMessage(), e);
        }
    }

    @Async("taskExecutor")
    public void subirImagen(Long usuarioId, MultipartFile imagen) {
        try {
            String fotoUrl = storageService.subirImagen(imagen);

            Usuario usuario = usuarioService.buscarPorId(usuarioId,
                    new RuntimeException("Usuario no encontrado"));
            usuario.setFotoPerfilUrl(fotoUrl);
            usuarioService.guardar(usuario);
        } catch (Exception e) {
            log.error("❌ [ASYNC] Error subir imagen: {}", e.getMessage());
        }
    }

    @Async
    public void enviarEmailAsync(String email, String token) {
        try {
            emailService.enviarEmailVerificacion(email, token);
        } catch (Exception e) {
            System.out.println("Error al enviar email a {}: {}\", email, e.getMessage()");
        }
    }
}