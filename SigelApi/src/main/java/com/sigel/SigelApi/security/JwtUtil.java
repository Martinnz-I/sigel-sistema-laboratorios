package com.sigel.SigelApi.security;

import com.sigel.SigelApi.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long tokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private SecretKey signingKey;

    private SecretKey getSigningKey() {
        if (signingKey == null) {
            signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        }
        return signingKey;
    }

    public String generarToken(Long usuarioId, String email, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("rol", rol);
        return crearToken(claims, usuarioId, tokenExpiration);
    }

    public String generarRefreshToken(Long usuarioId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("tipo", "REFRESH");
        return crearToken(claims, usuarioId, refreshTokenExpiration);
    }

    private String crearToken(Map<String, Object> claims, Long usuarioId, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(usuarioId.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    public Claims obtenerClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.debug("Error al obtener claims: {}", e.getMessage());
            return null;
        }
    }

    public Long obtenerUsuarioId(String token) {
        Claims claims = obtenerClaims(token);
        if (claims == null) {
            return null;
        }
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            log.error("Error al parsear usuarioId: {}", e.getMessage());
            return null;
        }
    }

    public Usuario obtenerUsuarioToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof Usuario) {
                Usuario usuario = (Usuario) principal;
                log.info("✅ Usuario encontrado: {}", usuario.getEmail());
                return usuario;
            } else {
                log.warn("⚠️ Principal NO es Usuario, es: {}", principal);
            }
        } else {
            log.warn("⚠️ No hay autenticación o no está autenticado");
        }

        return null;
    }

    public String obtenerEmail(String token) {
        Claims claims = obtenerClaims(token);
        return claims != null ? claims.get("email", String.class) : null;
    }

    public String obtenerRol(String token) {
        Claims claims = obtenerClaims(token);
        return claims != null ? claims.get("rol", String.class) : null;
    }

    public Date obtenerFechaExpiracion(String token) {
        Claims claims = obtenerClaims(token);
        return claims != null ? claims.getExpiration() : null;
    }

    public boolean esTokenExpirado(String token) {
        Date fechaExpiracion = obtenerFechaExpiracion(token);
        return fechaExpiracion == null || fechaExpiracion.before(new Date());
    }
}