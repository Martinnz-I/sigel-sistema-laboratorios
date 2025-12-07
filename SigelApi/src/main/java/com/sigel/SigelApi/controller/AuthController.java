package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.dto.*;
import com.sigel.SigelApi.service.AuthService;
import com.sigel.SigelApi.service.EmailVerificationService;
import com.sigel.SigelApi.service.PasswordRecoveryService;
import com.sigel.SigelApi.service.RegistroService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegistroService registroService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordRecoveryService passwordRecoveryService;

    @PostMapping(value = "/registro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> registro(
            @RequestPart("datos") @Valid RegistroRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        String mensaje = registroService.registrar(request, imagen);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, mensaje));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response, "Login exitoso"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refrescarToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        System.out.println("Refrescando token");
        AuthResponse response = authService.refrescarToken(request);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response, "Token refrescado exitosamente"));
    }

    @PostMapping("/logout")
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
    public ResponseEntity<ApiResponse<Void>> verificarEmail(
            @Valid @RequestBody VerificacionEmailRequest request
    ) {
        emailVerificationService.verificarEmail(request.getToken());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Email verificado exitosamente"));
    }

    @PostMapping("/reenviar-verificacion")
    public ResponseEntity<ApiResponse<Void>> reenviarVerificacion(
            @Valid @RequestBody ReenviarVerificacionRequest request
    ) {
        emailVerificationService.reenviarVerificacion(request.getEmail());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Email de verificación enviado"));
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<ApiResponse<Void>> recuperarPassword(
            @Valid @RequestBody RecuperarPasswordRequest request
    ) {
        passwordRecoveryService.solicitarRecuperacionPassword(request.getEmail());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Si el email existe, recibirás instrucciones para recuperar tu contraseña"));
    }

    @PostMapping("/restablecer-password")
    public ResponseEntity<ApiResponse<Void>> restablecerPassword(
            @Valid @RequestBody RestablecerPasswordRequest request
    ) {
        passwordRecoveryService.restablecerPassword(request.getToken(), request.getNuevaPassword());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Contraseña restablecida exitosamente"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity
                .ok()
                .body(ApiResponse.success("OK", "Servicio de autenticación DISPONIBLE"));
    }

    private String extraerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Header Authorization inválido. Formato esperado: Bearer <token>");
        }
        return authHeader.substring(7);
    }
}