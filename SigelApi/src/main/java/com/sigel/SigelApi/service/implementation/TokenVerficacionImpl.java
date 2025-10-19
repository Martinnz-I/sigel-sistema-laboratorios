package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.model.TokenVerificacion;

public interface TokenVerficacionImpl {
    TokenVerificacion buscarPorToken(String token);

    TokenVerificacion guardar(TokenVerificacion tokenVerificacion);

    void eliminarPorUsuarioId(Long usuarioId);
}