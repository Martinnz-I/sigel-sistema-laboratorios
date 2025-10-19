package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.Sesion;
import com.sigel.SigelApi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, Long> {
    Optional<Sesion> findByToken(String token);
    Optional<Sesion> findByRefreshToken(String refreshToken);
    Optional<Sesion> findByTokenAndActivo(String token, boolean activo);
    List<Sesion> findByUsuarioAndActivoTrue(Usuario usuario);
    void deleteByUsuarioAndActivoFalse(Usuario usuario);
    void deleteByUsuarioAndActivoTrue(Usuario usuario);
}