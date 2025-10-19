package com.sigel.SigelApi.service;

import com.sigel.SigelApi.exceptions.AuthenticationException;
import com.sigel.SigelApi.model.TokenVerificacion;
import com.sigel.SigelApi.repository.TokenVerificacionRepository;
import com.sigel.SigelApi.service.implementation.TokenVerficacionImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenVerificacionService implements TokenVerficacionImpl {
    private final TokenVerificacionRepository repository;

    @Override
    public TokenVerificacion buscarPorToken(String token) {
        return repository.findByToken(token).orElseThrow(() -> new AuthenticationException("Token de verificación inválido"));
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