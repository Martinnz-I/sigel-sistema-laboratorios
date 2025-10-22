package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.PasswordRecoveryToken;
import com.sigel.SigelApi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordRecoveryTokenRepository extends JpaRepository<PasswordRecoveryToken, Long> {
    Optional<PasswordRecoveryToken> findByToken(String token);
    List<PasswordRecoveryToken> findByUsuarioAndUtilizadoFalse(Usuario usuario);
    List<PasswordRecoveryToken> findByUsuarioAndUtilizadoFalseAndFechaExpiracionAfter(
            Usuario usuario,
            LocalDateTime fechaActual
    );
    long countByUsuarioAndUtilizadoFalseAndFechaExpiracionAfter(
            Usuario usuario,
            LocalDateTime fechaActual
    );
    boolean existsByUsuarioAndUtilizadoFalseAndFechaExpiracionAfter(
            Usuario usuario,
            LocalDateTime fechaActual
    );
    void deleteByFechaExpiracionBefore(LocalDateTime fecha);

    @Modifying
    @Query("UPDATE PasswordRecoveryToken t SET t.utilizado = true " +
            "WHERE t.usuario = :usuario AND t.utilizado = false")
    int invalidarTokensDelUsuario(@Param("usuario") Usuario usuario);

    @Modifying
    @Query("DELETE FROM PasswordRecoveryToken t WHERE t.fechaExpiracion < :fecha")
    int eliminarTokensExpiradosConConteo(@Param("fecha") LocalDateTime fecha);
}
