package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByMatricula(String matricula);
    Optional<Usuario> findByClaveDocente(String claveDocente);
    boolean existsByEmail(String email);
    boolean existsByMatricula(String matricula);
    boolean existsByClaveDocente(String claveDocente);
}