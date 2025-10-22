package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.model.PasswordRecoveryToken;
import com.sigel.SigelApi.model.Usuario;

import java.util.List;

public interface PasswordRecoveryTokenImpl {
    String generarToken(Usuario usuario);

    PasswordRecoveryToken buscarPorToken(String token);

    void marcarComoUtilizado(PasswordRecoveryToken token);

    int invalidarTokensDelUsuario(Usuario usuario);

    List<PasswordRecoveryToken> buscarTokensNoUsados(Usuario usuario);

    List<PasswordRecoveryToken> buscarTokensValidos(Usuario usuario);

    boolean tieneTokenValido(Usuario usuario);

    long contarTokensActivos(Usuario usuario);

    PasswordRecoveryToken guardar(PasswordRecoveryToken token);
}
