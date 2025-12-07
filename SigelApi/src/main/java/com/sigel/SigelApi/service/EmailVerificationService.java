package com.sigel.SigelApi.service;

import com.sigel.SigelApi.asyncs.RegistroAsyncService;
import com.sigel.SigelApi.exceptions.AuthenticationException;
import com.sigel.SigelApi.exceptions.RegistroException;
import com.sigel.SigelApi.model.TokenVerificacion;
import com.sigel.SigelApi.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final UsuarioService usuarioService;
    private final TokenVerificacionService tokenVerificacionService;
    private final RegistroAsyncService registroAsyncService;

    public void verificarEmail(String tokenStr) {
        TokenVerificacion tokenVerificacion = validarTokenVerificacion(tokenStr);

        Usuario usuario = tokenVerificacion.getUsuario();
        usuario.setEmailVerificado(true);
        usuarioService.guardar(usuario);

        tokenVerificacion.setUtilizado(true);
        tokenVerificacionService.guardar(tokenVerificacion);
    }

    public void reenviarVerificacion(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email,
                new AuthenticationException("Usuario no encontrado"));

        if (usuario.isEmailVerificado()) {
            throw new RegistroException("El email ya está verificado");
        }

        // Invalidar token anterior si existe
        TokenVerificacion tokenAnterior = tokenVerificacionService
                .buscarPorUsuarioYNoUtilizado(usuario.getId());

        if (tokenAnterior != null) {
            tokenAnterior.setUtilizado(true);
            tokenVerificacionService.guardar(tokenAnterior);
        }

        String nuevoToken = tokenVerificacionService.generarTokenVerificacion(usuario);
        registroAsyncService.enviarEmailAsync(email, nuevoToken);
    }

    private TokenVerificacion validarTokenVerificacion(String token) {
        TokenVerificacion verificationToken = tokenVerificacionService.buscarPorToken(token);
        if (verificationToken.isUtilizado()) {
            throw new AuthenticationException("Este token ya ha sido utilizado");
        }
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException("Token de verificación expirado");
        }
        return verificationToken;
    }
}
