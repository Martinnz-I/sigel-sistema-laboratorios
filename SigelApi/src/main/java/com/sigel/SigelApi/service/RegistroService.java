package com.sigel.SigelApi.service;

import com.sigel.SigelApi.asyncs.RegistroAsyncService;
import com.sigel.SigelApi.dto.RegistroRequest;
import com.sigel.SigelApi.enums.UserRole;
import com.sigel.SigelApi.exceptions.RegistroException;
import com.sigel.SigelApi.model.Usuario;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RegistroService {
    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;
    private final TokenVerificacionService tokenVerificacionService;
    private final RegistroAsyncService registroAsyncService;

    private static final Pattern MATRICULA_PATTERN = Pattern.compile("^\\d+$");

    @Transactional
    public String registrar(RegistroRequest request, MultipartFile imagen) {
        validarRegistro(request);

        if (request.getRol() == UserRole.ALUMNO) {
            return registrarAlumnoOptimizado(request, imagen);
        }

        validarQueSeaAdmin();
        return registrarUsuarioConVerificacionAutomatica(request, imagen);
    }

    private String registrarAlumnoOptimizado(RegistroRequest request, MultipartFile imagen) {
        String passwordEncriptada = passwordEncoder.encode(request.getPassword());

        Usuario usuarioTemp = usuarioService.construir(request, passwordEncriptada, null);

        Usuario usuario = usuarioService.guardar(usuarioTemp);

        String tokenVerificacion = tokenVerificacionService.generarTokenVerificacion(usuario);

        registroAsyncService.procesarPostRegistro(
                usuario.getId(),
                imagen,
                usuario.getEmail(),
                tokenVerificacion
        );

        return "Has sido registrado exitosamente. Verifica tu email para activar tu cuenta";
    }

    private String registrarUsuarioConVerificacionAutomatica(RegistroRequest request,
                                                             MultipartFile imagen) {
        String passwordEncriptada = passwordEncoder.encode(request.getPassword());

        Usuario usuario = usuarioService.guardar(
                usuarioService.construir(request, passwordEncriptada, null)
        );

        usuario.setEmailVerificado(true);
        usuarioService.guardar(usuario);

        if (imagen != null && !imagen.isEmpty()) {
            registroAsyncService.subirImagen(usuario.getId(), imagen);
        }

        String rolUsuario = switch(usuario.getRol()) {
            case MAESTRO -> "Maestro";
            case ADMINISTRADOR -> "Administrador";
            default -> "Usuario";
        };

        return rolUsuario + " registrado exitosamente";
    }

    public void validarRegistro(RegistroRequest request) {
        validarEmail(request.getEmail());
        validarContrasenas(request.getPassword(), request.getConfirmarPassword());
        validarDatosRolEspecifico(request);
    }

    public void validarEmail(String email) {
        if (usuarioService.existeEmail(email)) {
            throw new RegistroException("El email ya está registrado");
        }
    }

    public void validarContrasenas(String password, String confirmarPassword) {
        if (!password.equals(confirmarPassword)) {
            throw new RegistroException("Las contraseñas no coinciden");
        }
    }

    public void validarDatosRolEspecifico(RegistroRequest request) {
        UserRole rol = request.getRol();
        switch (rol) {
            case ALUMNO -> validarMatricula(request.getMatricula());
            case MAESTRO, ADMINISTRADOR -> validarClaveDocente(request.getClaveDocente());
        }
    }

    public void validarQueSeaAdmin() {
        Usuario adminActual = obtenerUsuarioToken();
        if (adminActual == null) {
            throw new RegistroException("Se requiere autenticación para registrar maestros");
        }
        if (adminActual.getRol() != UserRole.ADMINISTRADOR) {
            throw new RegistroException("Solo un administrador puede registrar maestros");
        }
    }

    public void validarMatricula(String matricula) {
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

    public void validarClaveDocente(String claveDocente) {
        if (claveDocente == null || claveDocente.isBlank()) {
            throw new RegistroException("La clave docente es requerida");
        }
        if (usuarioService.existeClaveDocente(claveDocente)) {
            throw new RegistroException("La clave docente ya está registrada");
        }
    }

    private Usuario obtenerUsuarioToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Usuario) {
                return (Usuario) principal;
            }
        }
        return null;
    }
}