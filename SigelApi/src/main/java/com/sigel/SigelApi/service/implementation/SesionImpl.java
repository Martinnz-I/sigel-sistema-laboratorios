package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.model.Sesion;
import com.sigel.SigelApi.model.Usuario;

import java.util.List;

public interface SesionImpl {
    List<Sesion> buscarPorUsuarioActivo(Usuario usuario);

    Sesion buscarPorRefreshToken(String refreshToken, RuntimeException exception);

    Sesion buscarPorToken(String token, RuntimeException exception);

    boolean existeSesionActivaPorToken(String token);

    Sesion guardar(Sesion sesion);
}