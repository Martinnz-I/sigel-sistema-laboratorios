package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.dto.*;
import com.sigel.SigelApi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para autenticación, registro y gestión de sesiones de usuarios")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario en el sistema. El email debe ser único y se enviará un correo de verificación."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Error en validación o datos duplicados (email, matrícula o clave docente ya existen)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<Void>> registro(
            @Valid @RequestBody RegistroRequest request
    ) {
        String mensaje = authService.registrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, mensaje));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario mediante email, matrícula o clave docente y retorna tokens JWT (access token y refresh token)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login exitoso, retorna tokens de acceso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas o cuenta no verificada"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response, "Login exitoso"));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refrescar token de acceso",
            description = "Genera un nuevo access token utilizando un refresh token válido. Útil para mantener sesiones activas sin requerir login nuevamente."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token refrescado exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Refresh token inválido, expirado o revocado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refrescarToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        AuthResponse response = authService.refrescarToken(request);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response, "Token refrescado exitosamente"));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Cerrar sesión",
            description = "Cierra la sesión actual del usuario invalidando el access token proporcionado"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "Sesión cerrada exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o no autenticado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Header Authorization inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<Void> logout(
            @Parameter(description = "Token JWT en formato: Bearer <token>", required = true)
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        String token = extraerToken(authHeader);
        authService.logout(token);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/logout-todas")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Cerrar todas las sesiones",
            description = "Cierra todas las sesiones activas del usuario en todos los dispositivos"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "Todas las sesiones cerradas exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o usuario no autenticado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Header Authorization inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<Void> logoutTodas(
            @Parameter(description = "Token JWT en formato: Bearer <token>", required = true)
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        String token = extraerToken(authHeader);
        authService.logoutTodas(token);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/verificar-email")
    @Operation(
            summary = "Verificar email",
            description = "Verifica la dirección de email del usuario utilizando el token de verificación recibido por correo"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Email verificado exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Token de verificación inválido, expirado o ya utilizado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<Void>> verificarEmail(
            @Valid @RequestBody VerificacionEmailRequest request
    ) {
        authService.verificarEmail(request.getToken());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Email verificado exitosamente"));
    }

    @PostMapping("/reenviar-verificacion")
    @Operation(
            summary = "Reenviar email de verificación",
            description = "Envía un nuevo correo de verificación al usuario"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Email de verificación enviado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Email ya verificado o datos inválidos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<Void>> reenviarVerificacion(
            @Valid @RequestBody ReenviarVerificacionRequest request
    ) {
        authService.reenviarVerificacion(request.getEmail());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Email de verificación enviado"));
    }

    @PostMapping("/recuperar-password")
    @Operation(
            summary = "Solicitar recuperación de contraseña",
            description = "Envía un correo con un enlace para restablecer la contraseña"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Email de recuperación enviado (siempre retorna 200 por seguridad)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<Void>> recuperarPassword(
            @Valid @RequestBody RecuperarPasswordRequest request
    ) {
        authService.solicitarRecuperacionPassword(request.getEmail());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Si el email existe, recibirás instrucciones para recuperar tu contraseña"));
    }

    @PostMapping("/restablecer-password")
    @Operation(
            summary = "Restablecer contraseña",
            description = "Restablece la contraseña del usuario utilizando el token de recuperación"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Contraseña restablecida exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Token inválido, expirado o contraseña no cumple requisitos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<Void>> restablecerPassword(
            @Valid @RequestBody RestablecerPasswordRequest request
    ) {
        authService.restablecerPassword(request.getToken(), request.getNuevaPassword());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Contraseña restablecida exitosamente"));
    }

    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Verifica el estado y disponibilidad del servicio de autenticación"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Servicio disponible"
            )
    })
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity
                .ok()
                .body(ApiResponse.success("OK", "Servicio de autenticación disponible"));
    }

    /**
     * Extrae el token JWT del header Authorization
     */
    private String extraerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Header Authorization inválido. Formato esperado: Bearer <token>");
        }
        return authHeader.substring(7);
    }
}