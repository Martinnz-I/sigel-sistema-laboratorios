package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.TokenVerificacion;
import com.sigel.SigelApi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenVerificacionRepository extends JpaRepository<TokenVerificacion, Long> {
    Optional<TokenVerificacion> findByToken(String token);
    Optional<TokenVerificacion> findByUsuarioIdAndUtilizado(Long idUsuario, boolean utilizado);
    Optional<TokenVerificacion> findByUsuarioIdAndUtilizadoFalse(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}