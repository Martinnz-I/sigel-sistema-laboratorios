package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.model.TokenVerificacion;
import com.sigel.SigelApi.model.Usuario;

public interface TokenVerficacionImpl {
    TokenVerificacion buscarPorToken(String token);

    boolean buscarPorUsuarioYUtilizado(Long idUsuario);

    String generarTokenVerificacion(Usuario usuario);

    TokenVerificacion buscarPorUsuarioYNoUtilizado(Long usuarioId);

    TokenVerificacion guardar(TokenVerificacion tokenVerificacion);

    void eliminarPorUsuarioId(Long usuarioId);
}