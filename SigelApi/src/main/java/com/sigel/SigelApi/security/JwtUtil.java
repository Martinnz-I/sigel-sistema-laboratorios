package com.sigel.SigelApi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para manejo de JWT tokens
 * Genera, valida y extrae información de tokens JWT
 */
@Service
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long tokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    // Cache de la clave para no regenerarla en cada llamada
    private SecretKey signingKey;

    /**
     * Obtiene la clave secreta para firmar tokens (lazy initialization)
     * Mínimo 32 caracteres para HMAC-SHA512
     */
    private SecretKey getSigningKey() {
        if (signingKey == null) {
            signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        }
        return signingKey;
    }

    /**
     * Genera un JWT token con información del usuario
     * Token expira según la configuración
     */
    public String generarToken(Long usuarioId, String email, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("rol", rol);
        return crearToken(claims, usuarioId, tokenExpiration);
    }

    /**
     * Genera un refresh token
     * Token expira según la configuración
     */
    public String generarRefreshToken(Long usuarioId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("tipo", "REFRESH");
        return crearToken(claims, usuarioId, refreshTokenExpiration);
    }

    /**
     * Crea un token JWT con los claims y tiempo de expiración especificados
     */
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

    /**
     * Valida que un token JWT sea válido y no haya expirado
     */
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

    /**
     * Extrae las claims (información) del token
     */
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

    /**
     * Obtiene el ID del usuario desde el token
     * Retorna Long para mantener coherencia con el tipo de datos
     */
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

    /**
     * Obtiene el email desde el token
     */
    public String obtenerEmail(String token) {
        Claims claims = obtenerClaims(token);
        return claims != null ? claims.get("email", String.class) : null;
    }

    /**
     * Obtiene el rol desde el token
     */
    public String obtenerRol(String token) {
        Claims claims = obtenerClaims(token);
        return claims != null ? claims.get("rol", String.class) : null;
    }

    /**
     * Obtiene la fecha de expiración del token
     */
    public Date obtenerFechaExpiracion(String token) {
        Claims claims = obtenerClaims(token);
        return claims != null ? claims.getExpiration() : null;
    }

    /**
     * Verifica si el token ha expirado
     */
    public boolean esTokenExpirado(String token) {
        Date fechaExpiracion = obtenerFechaExpiracion(token);
        return fechaExpiracion == null || fechaExpiracion.before(new Date());
    }
}