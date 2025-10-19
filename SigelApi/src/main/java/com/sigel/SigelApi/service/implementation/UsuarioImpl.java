package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.dto.RegistroRequest;
import com.sigel.SigelApi.model.Usuario;

import java.util.List;

public interface UsuarioImpl {
    List<Usuario> listar();

    Usuario buscarPorId(Long id, RuntimeException exception);

    Usuario buscarPorEmail(String email, RuntimeException exception);

    Usuario buscarPorMatricula(String matricula, RuntimeException exception);

    Usuario buscarPorClaveDocente(String claveDocente, RuntimeException exception);

    boolean existeEmail(String email);

    boolean existeMatricula(String matricula);

    boolean existeClaveDocente(String claveDocente);

    void desactivarUsuario(Usuario usuario);

    Usuario guardar(Usuario usuario);

    Usuario construir(RegistroRequest request, String passwordHash);

    void aplicarUltimoAcceso(Usuario usuario);
}
