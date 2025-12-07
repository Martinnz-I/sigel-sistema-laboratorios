package com.sigel.SigelApi.service;

import com.sigel.SigelApi.dto.*;
import com.sigel.SigelApi.enums.TipoCredencial;
import com.sigel.SigelApi.exceptions.AuthenticationException;
import com.sigel.SigelApi.exceptions.BadRequestException;
import com.sigel.SigelApi.model.Sesion;
import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UsuarioService usuarioService;
    private final SesionService sesionService;
    private final TokenVerificacionService tokenVerificacionService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest request;

    private static final String CREDENCIALES_INVALIDAS = "Credenciales inválidas";
    private static final String TOKEN_INVALIDO = "Token inválido";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern MATRICULA_PATTERN = Pattern.compile("^\\d+$");

    public AuthResponse login(LoginRequest request) {
        TipoCredencial tipo = determinarTipo(request.getCredenciales());

        Usuario usuario = switch (tipo) {
            case EMAIL -> usuarioService.buscarPorEmail(request.getCredenciales(),
                    new AuthenticationException(CREDENCIALES_INVALIDAS));
            case MATRICULA -> usuarioService.buscarPorMatricula(request.getCredenciales(),
                    new AuthenticationException(CREDENCIALES_INVALIDAS));
            case CLAVE_DOCENTE -> usuarioService.buscarPorClaveDocente(request.getCredenciales(),
                    new AuthenticationException(CREDENCIALES_INVALIDAS));
        };

        validarUsuarioActivo(usuario);
        tokenVerificacionService.buscarPorUsuarioYUtilizado(usuario.getId());

        if(!usuario.isEmailVerificado()) {
            throw new BadRequestException("Antes de iniciar sesión, debes verificar tu correo electrónico.");
        }

        validarPassword(usuario, request.getPassword());
        usuarioService.aplicarUltimoAcceso(usuario);

        return crearSesionYToken(usuario);
    }

    public AuthResponse refrescarToken(RefreshTokenRequest request) {
        Sesion sesion = sesionService.buscarPorRefreshToken(request.getRefreshToken(),
                new AuthenticationException("Refresh token inválido"));

        validarSesionActiva(sesion);
        Usuario usuario = sesion.getUsuario();

        String nuevoToken = jwtUtil.generarToken(usuario.getId(), usuario.getEmail(),
                usuario.getRol().getAuthority());
        String nuevoRefreshToken = jwtUtil.generarRefreshToken(usuario.getId(), usuario.getEmail());

        LocalDateTime expiraEn = convertirDateALocalDateTime(jwtUtil.obtenerFechaExpiracion(nuevoRefreshToken));

        sesion.setToken(nuevoToken);
        sesion.setRefreshToken(nuevoRefreshToken);
        sesion.setExpiraEn(expiraEn);
        sesionService.guardar(sesion);

        return AuthResponse.from(usuario, nuevoToken, nuevoRefreshToken, expiraEn);
    }

    public void logout(String token) {
        Sesion sesion = sesionService.buscarPorToken(token,
                new AuthenticationException(TOKEN_INVALIDO));
        sesion.setActivo(false);
        sesionService.guardar(sesion);
    }

    public long logoutTodas(String token) {
        Sesion sesionBusqueda = sesionService.buscarPorToken(token,
                new AuthenticationException(TOKEN_INVALIDO));

        Usuario usuario = usuarioService.buscarPorId(sesionBusqueda.getUsuario().getId(),
                new AuthenticationException("Usuario no encontrado"));

        List<Sesion> sesionesActivas = sesionService.buscarPorUsuarioActivo(usuario);

        sesionesActivas.forEach(sesion -> {
            sesion.setActivo(false);
            sesionService.guardar(sesion);
        });

        return sesionesActivas.size();
    }

    public void cerrarTodasLasSesionesDelUsuario(Usuario usuario) {
        List<Sesion> sesionesActivas = sesionService.buscarPorUsuarioActivo(usuario);
        sesionesActivas.forEach(sesion -> {
            sesion.setActivo(false);
            sesionService.guardar(sesion);
        });
        if (!sesionesActivas.isEmpty()) {
            log.info("Cerradas {} sesiones del usuario: {}", sesionesActivas.size(), usuario.getEmail());
        }
    }

    private TipoCredencial determinarTipo(String credencial) {
        if (EMAIL_PATTERN.matcher(credencial).matches()) {
            return TipoCredencial.EMAIL;
        }
        if (MATRICULA_PATTERN.matcher(credencial).matches()) {
            return TipoCredencial.MATRICULA;
        }
        return TipoCredencial.CLAVE_DOCENTE;
    }

    private void validarUsuarioActivo(Usuario usuario) {
        if (!usuario.getActivo()) {
            throw new AuthenticationException("Usuario Inactivo");
        }
    }

    private void validarPassword(Usuario usuario, String password) {
        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new AuthenticationException("Usuario inactivo");
        }
    }

    private void validarSesionActiva(Sesion sesion) {
        if (!sesion.isActivo()) {
            throw new AuthenticationException("Sesión inactiva");
        }
        if (sesion.getExpiraEn().isBefore(LocalDateTime.now())) {
            sesion.setActivo(false);
            sesionService.guardar(sesion);
            throw new AuthenticationException("Sesión expirada");
        }
    }

    private AuthResponse crearSesionYToken(Usuario usuario) {
        String token = jwtUtil.generarToken(usuario.getId(), usuario.getEmail(),
                usuario.getRol().getAuthority());
        String refreshToken = jwtUtil.generarRefreshToken(usuario.getId(), usuario.getEmail());

        String ipAddress = obtenerIpAddress();
        String userAgent = request.getHeader("User-Agent");
        LocalDateTime expiraEn = convertirDateALocalDateTime(jwtUtil.obtenerFechaExpiracion(refreshToken));

        Sesion sesion = Sesion.builder()
                .usuario(usuario)
                .token(token)
                .refreshToken(refreshToken)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiraEn(expiraEn)
                .activo(true)
                .build();

        sesionService.guardar(sesion);

        return AuthResponse.from(usuario, token, refreshToken, expiraEn);
    }

    private String obtenerIpAddress() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    private LocalDateTime convertirDateALocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}