package com.sigel.SigelApi.service;

import com.sigel.SigelApi.exceptions.AuthenticationException;
import com.sigel.SigelApi.model.TokenVerificacion;
import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.repository.TokenVerificacionRepository;
import com.sigel.SigelApi.service.implementation.TokenVerficacionImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenVerificacionService implements TokenVerficacionImpl {
    private final TokenVerificacionRepository repository;

    @Value("${jwt.token-verification-hours:24}")
    private long tokenVerificationHours;

    @Override
    public TokenVerificacion buscarPorToken(String token) {
        return repository.findByToken(token).orElseThrow(() -> new AuthenticationException("Token de verificación inválido"));
    }

    @Override
    public boolean buscarPorUsuarioYUtilizado(Long idUsuario) {
        return repository.findByUsuarioIdAndUtilizado(idUsuario, false).isPresent();
    }

    @Override
    public String generarTokenVerificacion(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(tokenVerificationHours);

        TokenVerificacion verificationToken = TokenVerificacion.builder()
                .token(token)
                .usuario(usuario)
                .expiryDate(expiryDate)
                .utilizado(false)
                .build();

        guardar(verificationToken);
        return token;
    }

    @Override
    public TokenVerificacion buscarPorUsuarioYNoUtilizado(Long usuarioId) {
        return repository.findByUsuarioIdAndUtilizadoFalse(usuarioId).orElseThrow(() ->
                new AuthenticationException("Antes de iniciar sesión, verifica tu correo electrónico. " +
                        "Te enviamos un enlace de verificación; revisa tu bandeja de entrada o carpeta de spam."));
    }

    @Override
    public TokenVerificacion guardar(TokenVerificacion tokenVerificacion) {
        return repository.save(tokenVerificacion);
    }

    @Override
    public void eliminarPorUsuarioId(Long usuarioId) {
        repository.deleteByUsuarioId(usuarioId);
    }
}