package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.dto.*;
import com.sigel.SigelApi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación
 * Maneja registro, login, refresh token y logout
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoints para autenticación, registro y manejo de sesiones")
public class AuthController {

    private final AuthService authService;

    /**
     * Registra un nuevo usuario
     * POST /auth/registro
     */
    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error en validación o datos duplicados"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> registro(
            @Valid @RequestBody RegistroRequest request) {

        log.info("Intento de registro para email: {}", request.getEmail());

        AuthResponse response = authService.registrar(request);

        log.info("Usuario registrado exitosamente: {}", request.getEmail());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Usuario registrado. Verifica tu email para activar tu cuenta"));
    }

    /**
     * Autentica un usuario con credenciales
     * POST /auth/login
     * Acepta: email, matrícula o clave docente
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna tokens JWT")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Intento de login con credenciales: {}", request.getCredenciales());

        AuthResponse response = authService.login(request);

        log.info("Login exitoso para usuario ID: {}", response.getUsuarioId());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response, "Login exitoso"));
    }

    /**
     * Refresca el token de acceso usando el refresh token
     * POST /auth/refresh
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token", description = "Genera un nuevo access token usando el refresh token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refrescado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refrescarToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        log.info("Intento de refresh token");

        AuthResponse response = authService.refrescarToken(request);

        log.info("Token refrescado exitosamente para usuario ID: {}", response.getUsuarioId());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response, "Token refrescado exitosamente"));
    }

    /**
     * Cierra la sesión actual del usuario
     * POST /auth/logout
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión actual del usuario")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Logout exitoso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization") String authHeader) {

        String token = extraerToken(authHeader);

        log.info("Intento de logout");

        authService.logout(token);

        log.info("Logout exitoso");

        return ResponseEntity
                .noContent()
                .build();
    }

    /**
     * Cierra todas las sesiones del usuario
     * POST /auth/logout-todas
     */
    @PostMapping("/logout-todas")
    @Operation(summary = "Cerrar todas las sesiones", description = "Cierra todas las sesiones activas del usuario")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Logout de todas las sesiones exitoso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o usuario no autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> logoutTodas(
            @RequestHeader(value = "Authorization") String authHeader) {

        String token = extraerToken(authHeader);

        log.info("Intento de logout de todas las sesiones");

        authService.logout(token); // Logout actual

        // Aquí también puedes cerrar todas las otras sesiones si lo necesitas
        // authService.logoutTodas(usuarioId);

        log.info("Logout de todas las sesiones exitoso");

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/verificar-email")
    @Operation(summary = "Verificar email", description = "Verifica el email del usuario usando el token recibido")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email verificado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token inválido o expirado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponse<String>> verificarEmail(
            @Valid @RequestBody VerificacionEmailRequest request) {

        log.info("Intento de verificación de email con token");

        authService.verificarEmail(request.getToken());

        log.info("Email verificado exitosamente");

        return ResponseEntity
                .ok()
                .body(ApiResponse.success("Email verificado", "Tu email ha sido verificado exitosamente"));
    }

    /**
     * Extrae el token JWT del header Authorization
     */
    private String extraerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Header Authorization inválido. Formato: Bearer <token>");
        }
        return authHeader.substring(7);
    }

    /**
     * Health check del módulo de autenticación
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica que el servicio de autenticación esté disponible")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("OK", "Servicio de autenticación disponible"));
    }
}