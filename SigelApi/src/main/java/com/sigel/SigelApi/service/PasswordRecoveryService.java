package com.sigel.SigelApi.service;

import com.sigel.SigelApi.exceptions.AuthenticationException;
import com.sigel.SigelApi.model.PasswordRecoveryToken;
import com.sigel.SigelApi.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {
    private final UsuarioService usuarioService;
    private final PasswordRecoveryTokenService passwordRecoveryTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public void solicitarRecuperacionPassword(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email,
                new AuthenticationException("Usuario no encontrado"));

        if (usuario == null) {
            return;
        }

        validarUsuarioActivo(usuario);
        passwordRecoveryTokenService.invalidarTokensDelUsuario(usuario);

        String token = passwordRecoveryTokenService.generarToken(usuario);

        // ⚡ Enviar email asíncronamente
        enviarEmailRecuperacionAsync(email, token);
    }

    @Async
    public void enviarEmailRecuperacionAsync(String email, String token) {
        try {
            emailService.enviarEmailRecuperacion(email, token);
        } catch (Exception e) {
            System.out.println("Error al enviar email de recuperación");
        }
    }

    public void restablecerPassword(String tokenStr, String nuevaPassword) {
        PasswordRecoveryToken token = passwordRecoveryTokenService.buscarPorToken(tokenStr);
        passwordRecoveryTokenService.validarToken(token);

        Usuario usuario = token.getUsuario();
        validarUsuarioActivo(usuario);

        String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
        usuario.setPasswordHash(passwordEncriptada);
        usuarioService.guardar(usuario);

        passwordRecoveryTokenService.marcarComoUtilizado(token);
        authService.cerrarTodasLasSesionesDelUsuario(usuario);
    }

    private void validarUsuarioActivo(Usuario usuario) {
        if (!usuario.getActivo()) {
            throw new AuthenticationException("Usuario Inactivo");
        }
    }
}