package com.sigel.SigelApi.service;

import com.sigel.SigelApi.exceptions.AuthenticationException;
import com.sigel.SigelApi.model.PasswordRecoveryToken;
import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.repository.PasswordRecoveryTokenRepository;
import com.sigel.SigelApi.service.implementation.PasswordRecoveryTokenImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryTokenService implements PasswordRecoveryTokenImpl {

    @Value("${jwt.password-recovery-hours:1}")
    private long passwordRecoveryHours;

    private static final String TOKEN_INVALIDO = "Token de recuperación inválido o expirado";
    private final PasswordRecoveryTokenRepository repository;

    @Override
    public String generarToken(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiraEn = LocalDateTime.now().plusHours(passwordRecoveryHours);

        PasswordRecoveryToken recoveryToken = PasswordRecoveryToken.builder()
                .token(token)
                .usuario(usuario)
                .fechaExpiracion(expiraEn)
                .utilizado(false)
                .build();

        repository.save(recoveryToken);

        return token;
    }

    @Override
    public PasswordRecoveryToken buscarPorToken(String token) {
        return repository.findByToken(token)
                .orElseThrow(() -> new AuthenticationException(TOKEN_INVALIDO));
    }

    @Override
    public void marcarComoUtilizado(PasswordRecoveryToken token) {
        token.setUtilizado(true);
        this.guardar(token);
    }

    @Override
    public int invalidarTokensDelUsuario(Usuario usuario) {
        return repository.invalidarTokensDelUsuario(usuario);
    }

    @Override
    public List<PasswordRecoveryToken> buscarTokensNoUsados(Usuario usuario) {
        return repository.findByUsuarioAndUtilizadoFalse(usuario);
    }

    @Override
    public List<PasswordRecoveryToken> buscarTokensValidos(Usuario usuario) {
        return repository.findByUsuarioAndUtilizadoFalseAndFechaExpiracionAfter(
                usuario,
                LocalDateTime.now()
        );
    }

    @Override
    public boolean tieneTokenValido(Usuario usuario) {
        return repository.existsByUsuarioAndUtilizadoFalseAndFechaExpiracionAfter(
                usuario,
                LocalDateTime.now()
        );
    }

    @Override
    public long contarTokensActivos(Usuario usuario) {
        return repository.countByUsuarioAndUtilizadoFalseAndFechaExpiracionAfter(
                usuario,
                LocalDateTime.now()
        );
    }

    @Override
    public PasswordRecoveryToken guardar(PasswordRecoveryToken token) {
        return repository.save(token);
    }

    public void validarToken(PasswordRecoveryToken token) {
        if (token.isUtilizado()) {
            throw new AuthenticationException("Este token ya ha sido utilizado");
        }

        if (token.estaExpirado()) {
            throw new AuthenticationException("El token de recuperación ha expirado");
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void limpiarTokensExpirados() {
        int eliminados = repository.eliminarTokensExpiradosConConteo(LocalDateTime.now());
    }
}
