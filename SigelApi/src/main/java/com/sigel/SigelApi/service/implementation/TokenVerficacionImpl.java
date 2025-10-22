package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.model.TokenVerificacion;

public interface TokenVerficacionImpl {
    TokenVerificacion buscarPorToken(String token);

    boolean buscarPorUsuarioYUtilizado(Long idUsuario);

    TokenVerificacion buscarPorUsuarioYNoUtilizado(Long usuarioId);

    TokenVerificacion guardar(TokenVerificacion tokenVerificacion);

    void eliminarPorUsuarioId(Long usuarioId);
}