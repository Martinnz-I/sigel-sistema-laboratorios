package com.sigel.SigelApi.service;

import com.sigel.SigelApi.model.Sesion;
import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.repository.SesionRepository;
import com.sigel.SigelApi.service.implementation.SesionImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SesionService implements SesionImpl {
    private final SesionRepository repository;

    @Override
    public List<Sesion> buscarPorUsuarioActivo(Usuario usuario) {
        return repository.findByUsuarioAndActivoTrue(usuario);
    }

    @Override
    public Sesion buscarPorRefreshToken(String refreshToken, RuntimeException exception) {
        return repository.findByRefreshToken(refreshToken).orElseThrow(() -> exception);
    }

    @Override
    public Sesion buscarPorToken(String token, RuntimeException exception) {
        return repository.findByToken(token).orElseThrow(() -> exception);
    }

    @Override
    public boolean existeSesionActivaPorToken(String token) {
        return repository.findByTokenAndActivo(token, true).isPresent();
    }

    @Override
    public Sesion guardar(Sesion sesion) {
        return repository.save(sesion);
    }
}