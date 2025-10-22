package com.sigel.SigelApi.security;

import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.service.JwtUserDetailsService;
import com.sigel.SigelApi.service.SesionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro JWT que valida el token en cada request
 * Extrae el usuario desde el token y lo coloca en el contexto de seguridad
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtUtil jwtUtil;
    private final SesionService sesionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (validarYConfigurarAutenticacion(token)) {
                    log.debug("Token validado exitosamente");
                } else {
                    log.debug("Fallo la validación del token");
                }
            } catch (Exception e) {
                log.debug("Error validando token: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Valida el token y configura la autenticación si es válido
     * Retorna true si la autenticación fue exitosa
     */
    private boolean validarYConfigurarAutenticacion(String token) {
        // 1. Validar que el token tenga formato correcto
        if (!jwtUtil.validarToken(token)) {
            return false;
        }

        // 2. Validar que no haya expirado
        if (jwtUtil.esTokenExpirado(token)) {
            return false;
        }

        // 3. Obtener el ID del usuario desde el token
        Long usuarioId = jwtUtil.obtenerUsuarioId(token);
        if (usuarioId == null) {
            return false;
        }

        // 4. Obtener el usuario
        Usuario usuario = jwtUserDetailsService.obtenerUsuarioDesdeToken(token);
        if (usuario == null) {
            return false;
        }

        // 5. Validar que el usuario esté activo
        if (!usuario.getActivo()) {
            return false;
        }

        // 6. Validar que la sesión exista y esté activa en BD
        if (!sesionService.existeSesionActivaPorToken(token)) {
            return false;
        }

        // 7. Configurar la autenticación
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        List.of(new SimpleGrantedAuthority(usuario.getRol().getAuthority()))
                );

        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("Usuario autenticado: {}, rol: {}", usuario.getEmail(), usuario.getRol().getAuthority());
        return true;
    }
}