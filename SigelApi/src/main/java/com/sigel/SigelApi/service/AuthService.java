package com.sigel.SigelApi.service;

import com.sigel.SigelApi.dto.AuthResponse;
import com.sigel.SigelApi.dto.LoginRequest;
import com.sigel.SigelApi.dto.RefreshTokenRequest;
import com.sigel.SigelApi.dto.RegistroRequest;
import com.sigel.SigelApi.enums.TipoCredencial;
import com.sigel.SigelApi.enums.UserRole;
import com.sigel.SigelApi.exceptions.AuthenticationException;
import com.sigel.SigelApi.exceptions.RegistroException;
import com.sigel.SigelApi.model.PasswordRecoveryToken;
import com.sigel.SigelApi.model.Sesion;
import com.sigel.SigelApi.model.TokenVerificacion;
import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final UsuarioService usuarioService;
    private final SesionService sesionService;
    private final EmailService emailService;
    private final TokenVerificacionService tokenVerificacionService;
    private final PasswordRecoveryTokenService passwordRecoveryTokenService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest request;

    @Value("${jwt.token-verification-hours:24}")
    private long tokenVerificationHours;

    private static final String CREDENCIALES_INVALIDAS = "Credenciales inválidas";
    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    private static final String TOKEN_INVALIDO = "Token inválido";
    private static final String USUARIO_INACTIVO = "Usuario inactivo";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern MATRICULA_PATTERN = Pattern.compile("^\\d+$");


    /**
     * Registra un nuevo usuario
     */
    public String registrar(RegistroRequest request) {
        validarRegistro(request);

        // Si es ALUMNO, puede registrarse sin autenticación
        if (request.getRol() == UserRole.ALUMNO) {
            return registrarAlumno(request);
        }

        // Si no es ALUMNO, requiere que sea un admin quien lo registre
        validarQueSeaAdmin();
        return registrarUsuarioConVerificacionAutomatica(request);
    }

    private String registrarAlumno(RegistroRequest request) {
        String passwordEncriptada = passwordEncoder.encode(request.getPassword());
        Usuario usuario = usuarioService.guardar(
                usuarioService.construir(request, passwordEncriptada)
        );

        String tokenVerificacion = generarTokenVerificacion(usuario);
        emailService.enviarEmailVerificacion(usuario.getEmail(), tokenVerificacion);

        log.info("Alumno registrado. Email de verificación enviado a: {}", usuario.getEmail());

        return "Has sido registrado exitosamente. Verifica tu email para activar tu cuenta";
    }

    /**
     * Registra un usuario (MAESTRO/admin) - Solo admin puede hacerlo
     */
    private String registrarUsuarioConVerificacionAutomatica(RegistroRequest request) {
        String passwordEncriptada = passwordEncoder.encode(request.getPassword());
        Usuario usuario = usuarioService.guardar(
                usuarioService.construir(request, passwordEncriptada)
        );

        // Para maestros/admin, email se verifica automáticamente
        usuario.setEmailVerificado(true);
        usuarioService.guardar(usuario);

        log.info("Usuario {} registrado por admin. Email: {}", request.getRol(), usuario.getEmail());

        String rolUsuario = switch(usuario.getRol()) {
            case MAESTRO -> "Maestro";
            case ADMINISTRADOR -> "Administrador";
            default -> "Usuario";
        };

        return rolUsuario + "registrado exitosamente";
    }

    /**
     * Verifica el email del usuario con un token
     */
    public void verificarEmail(String tokenStr) {
        TokenVerificacion tokenVerificacion = validarTokenVerificacion(tokenStr);

        Usuario usuario = tokenVerificacion.getUsuario();
        usuario.setEmailVerificado(true);
        usuarioService.guardar(usuario);

        tokenVerificacion.setUtilizado(true);
        tokenVerificacionService.guardar(tokenVerificacion);

        log.info("Email verificado exitosamente para usuario: {}", usuario.getEmail());
    }

    /**
     * Reenvía el email de verificación al usuario
     */
    public void reenviarVerificacion(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email,
                new AuthenticationException(USUARIO_NO_ENCONTRADO));

        if (usuario.getEmailVerificado()) {
            throw new RegistroException("El email ya está verificado");
        }

        // Invalidar token anterior si existe
        TokenVerificacion tokenAnterior = tokenVerificacionService
                .buscarPorUsuarioYNoUtilizado(usuario.getId());

        if (tokenAnterior != null) {
            tokenAnterior.setUtilizado(true);
            tokenVerificacionService.guardar(tokenAnterior);
        }

        // Generar nuevo token y enviar email
        String nuevoToken = generarTokenVerificacion(usuario);
        emailService.enviarEmailVerificacion(email, nuevoToken);

        log.info("Email de verificación reenviado a: {}", email);
    }

    /**
     * Solicita la recuperación de contraseña
     * Genera un token y lo envía por email
     */
    public void solicitarRecuperacionPassword(String email) {
        // Buscar usuario - Si no existe, no revelar por seguridad
        Usuario usuario = usuarioService.buscarPorEmail(email, new AuthenticationException(USUARIO_NO_ENCONTRADO));

        if (usuario == null) {
            return;
        }

        // Validar que el usuario esté activo
        validarUsuarioActivo(usuario);

        // Invalidar tokens anteriores del usuario
        passwordRecoveryTokenService.invalidarTokensDelUsuario(usuario);

        // Generar y guardar nuevo token
        String token = passwordRecoveryTokenService.generarToken(usuario);

        // Enviar email con el token
        emailService.enviarEmailRecuperacion(email, token);
    }

    /**
     * Restablece la contraseña usando un token de recuperación
     */
    public void restablecerPassword(String tokenStr, String nuevaPassword) {
        // Buscar y validar el token
        PasswordRecoveryToken token = passwordRecoveryTokenService.buscarPorToken(tokenStr);
        passwordRecoveryTokenService.validarToken(token);

        // Obtener usuario
        Usuario usuario = token.getUsuario();

        // Validar que el usuario esté activo
        validarUsuarioActivo(usuario);

        // Cambiar contraseña
        String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
        usuario.setPasswordHash(passwordEncriptada);
        usuarioService.guardar(usuario);

        // Marcar token como usado
        passwordRecoveryTokenService.marcarComoUtilizado(token);

        // Cerrar todas las sesiones activas del usuario por seguridad
        cerrarTodasLasSesionesDelUsuario(usuario);
    }


    /**
     * Autentica un usuario con email, matrícula o clave docente
     */
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

        if(!usuario.getEmailVerificado()) {
            throw new AuthenticationException("Antes de iniciar sesión, debes verificar tu correo electrónico.");
        }

        validarPassword(usuario, request.getPassword());

        usuarioService.aplicarUltimoAcceso(usuario);

        return crearSesionYToken(usuario);
    }

    /**
     * Refresca un token usando el Refresh Token
     */
    public AuthResponse refrescarToken(RefreshTokenRequest request) {
        Sesion sesion = sesionService.buscarPorRefreshToken(request.getRefreshToken(),
                new AuthenticationException("Refresh token inválido"));

        validarSesionActiva(sesion);

        Usuario usuario = sesion.getUsuario();

        // Generar nuevos tokens
        String nuevoToken = jwtUtil.generarToken(usuario.getId(), usuario.getEmail(),
                usuario.getRol().getAuthority());
        String nuevoRefreshToken = jwtUtil.generarRefreshToken(usuario.getId(), usuario.getEmail());

        // Actualizar sesión
        LocalDateTime expiraEn = convertirDateALocalDateTime(jwtUtil.obtenerFechaExpiracion(nuevoRefreshToken));

        sesion.setToken(nuevoToken);
        sesion.setRefreshToken(nuevoRefreshToken);
        sesion.setExpiraEn(expiraEn);
        sesionService.guardar(sesion);

        return construirAuthResponse(usuario, nuevoToken, nuevoRefreshToken, expiraEn);
    }

    /**
     * Cierra la sesión del usuario
     */
    public void logout(String token) {
        Sesion sesion = sesionService.buscarPorToken(token,
                new AuthenticationException(TOKEN_INVALIDO));

        sesion.setActivo(false);
        sesionService.guardar(sesion);
    }

    /**
     * Cierra todas las sesiones de un usuario
     */
    public long logoutTodas(String token) {
        Sesion sesionBusqueda = sesionService.buscarPorToken(token,
                new AuthenticationException(TOKEN_INVALIDO));

        Usuario usuario = usuarioService.buscarPorId(sesionBusqueda.getUsuario().getId(),
                new AuthenticationException(USUARIO_NO_ENCONTRADO));

        List<Sesion> sesionesActivas = sesionService.buscarPorUsuarioActivo(usuario);

        sesionesActivas.forEach(sesion -> {
            sesion.setActivo(false);
            sesionService.guardar(sesion);
        });

        return sesionesActivas.size();
    }

    /**
     * Valida un token y retorna información del usuario
     */
    public Usuario validarYObtenerUsuario(String token) {
        if (!jwtUtil.validarToken(token)) {
            throw new AuthenticationException(TOKEN_INVALIDO);
        }

        Long usuarioId = jwtUtil.obtenerUsuarioId(token);
        if (usuarioId == null) {
            throw new AuthenticationException(TOKEN_INVALIDO);
        }

        Usuario usuario = usuarioService.buscarPorId(usuarioId,
                new AuthenticationException(USUARIO_NO_ENCONTRADO));

        validarUsuarioActivo(usuario);

        Sesion sesion = sesionService.buscarPorToken(token,
                new AuthenticationException("Sesión no encontrada"));

        validarSesionActiva(sesion);

        return usuario;
    }

    /**
     * Genera un token de verificación para el usuario
     */
    private String generarTokenVerificacion(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(tokenVerificationHours);

        TokenVerificacion verificationToken = TokenVerificacion.builder()
                .token(token)
                .usuario(usuario)
                .expiryDate(expiryDate)
                .utilizado(false)
                .build();

        tokenVerificacionService.guardar(verificationToken);

        return token;
    }

    /**
     * Determina el tipo de credencial (email, matrícula o clave docente)
     */
    private TipoCredencial determinarTipo(String credencial) {
        if (EMAIL_PATTERN.matcher(credencial).matches()) {
            return TipoCredencial.EMAIL;
        }
        if (MATRICULA_PATTERN.matcher(credencial).matches()) {
            return TipoCredencial.MATRICULA;
        }
        return TipoCredencial.CLAVE_DOCENTE;
    }

    /**
     * Crea una sesión y genera tokens
     */
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

        return construirAuthResponse(usuario, token, refreshToken, expiraEn);
    }

    /**
     * Construye la respuesta de autenticación
     */
    private AuthResponse construirAuthResponse(Usuario usuario, String token,
                                               String refreshToken, LocalDateTime expiraEn) {
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenExpira(expiraEn)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellidoPat(usuario.getApellidoPaterno())
                .apellidoMat(usuario.getApellidoMaterno())
                .rol(usuario.getRol().getAuthority())
                .build();
    }

    /**
     * Obtiene la dirección IP del cliente, considerando proxies
     */
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

    /**
     * Convierte Date a LocalDateTime
     */
    private LocalDateTime convertirDateALocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private void validarQueSeaAdmin() {
        Usuario adminActual = obtenerUsuarioToken();  // ← Usa tu método

        if (adminActual == null) {
            throw new RegistroException("Se requiere autenticación para registrar maestros");
        }

        if (adminActual.getRol() != UserRole.ADMINISTRADOR) {
            throw new RegistroException("Solo un administrador puede registrar maestros");
        }
    }

    /**
     * Valida los datos del registro
     */
    private void validarRegistro(RegistroRequest request) {
        validarEmail(request.getEmail());
        validarContrasenas(request.getPassword(), request.getConfirmarPassword());
        validarDatosRolEspecifico(request);
    }

    /**
     * Valida el email (formato y unicidad)
     */
    private void validarEmail(String email) {
        if (usuarioService.existeEmail(email)) {
            throw new RegistroException("El email ya está registrado");
        }
    }

    /**
     * Valida que las contraseñas coincidan
     */
    private void validarContrasenas(String password, String confirmarPassword) {
        if (!password.equals(confirmarPassword)) {
            throw new RegistroException("Las contraseñas no coinciden");
        }
    }

    /**
     * Valida los datos específicos del rol (matrícula para alumnos, clave docente para maestros)
     */
    private void validarDatosRolEspecifico(RegistroRequest request) {
        UserRole rol = request.getRol();

        switch (rol) {
            case ALUMNO -> validarMatricula(request.getMatricula());
            case MAESTRO, ADMINISTRADOR -> validarClaveDocente(request.getClaveDocente());
        }
    }

    /**
     * Valida la matrícula (formato y unicidad)
     */
    private void validarMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new RegistroException("La matrícula es requerida");
        }

        if (!MATRICULA_PATTERN.matcher(matricula).matches()) {
            throw new RegistroException("La matrícula debe contener solo números");
        }

        if (usuarioService.existeMatricula(matricula)) {
            throw new RegistroException("La matrícula ya está registrada");
        }
    }

    /**
     * Valida la clave docente (formato y unicidad)
     */
    private void validarClaveDocente(String claveDocente) {
        if (claveDocente == null || claveDocente.isBlank()) {
            throw new RegistroException("La clave docente es requerida");
        }

        if (usuarioService.existeClaveDocente(claveDocente)) {
            throw new RegistroException("La clave docente ya está registrada");
        }
    }

    /**
     * Valida que el token de verificación sea válido
     */
    private TokenVerificacion validarTokenVerificacion(String token) {
        TokenVerificacion verificationToken = tokenVerificacionService.buscarPorToken(token);

        if (verificationToken.getUtilizado()) {
            throw new AuthenticationException("Este token ya ha sido utilizado");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException("Token de verificación expirado");
        }

        return verificationToken;
    }

    /**
     * Valida que el usuario esté activo
     */
    private void validarUsuarioActivo(Usuario usuario) {
        if (!usuario.getActivo()) {
            throw new AuthenticationException(USUARIO_INACTIVO);
        }
    }

    /**
     * Valida que la sesión esté activa y no haya expirado
     */
    private void validarSesionActiva(Sesion sesion) {
        if (!sesion.getActivo()) {
            throw new AuthenticationException("Sesión inactiva");
        }

        if (sesion.getExpiraEn().isBefore(LocalDateTime.now())) {
            sesion.setActivo(false);
            sesionService.guardar(sesion);
            throw new AuthenticationException("Sesión expirada");
        }
    }

    /**
     * Valida la contraseña del usuario
     */
    private void validarPassword(Usuario usuario, String password) {
        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new AuthenticationException(CREDENCIALES_INVALIDAS);
        }
    }

    private Usuario obtenerUsuarioToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            // Valida que no sea "anonymousUser" (Spring Security default)
            if (principal instanceof Usuario) {
                return (Usuario) principal;
            }
        }

        return null;
    }

    private void cerrarTodasLasSesionesDelUsuario(Usuario usuario) {
        List<Sesion> sesionesActivas = sesionService.buscarPorUsuarioActivo(usuario);

        sesionesActivas.forEach(sesion -> {
            sesion.setActivo(false);
            sesionService.guardar(sesion);
        });

        if (!sesionesActivas.isEmpty()) {
            log.info("Cerradas {} sesiones del usuario: {}", sesionesActivas.size(), usuario.getEmail());
        }
    }
}