package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.TokenVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenVerificacionRepository extends JpaRepository<TokenVerificacion, Long> {
    Optional<TokenVerificacion> findByToken(String token);
    void deleteByUsuarioId(Long usuarioId);
}