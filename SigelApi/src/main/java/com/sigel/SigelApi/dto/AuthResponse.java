package com.sigel.SigelApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private LocalDateTime tokenExpira;
    private String email;
    private String nombre;
    private String apellidoPat;
    private String apellidoMat;
    private String rol;
}