package com.sigel.SigelApi.service;

import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public JwtUserDetailsService(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    public Usuario obtenerUsuarioDesdeToken(String token) {
        if (!jwtUtil.validarToken(token)) {
            return null;
        }

        Long usuarioId = jwtUtil.obtenerUsuarioId(token);
        return usuarioId != null ? usuarioService.buscarPorId(usuarioId, null) : null;
    }
}
