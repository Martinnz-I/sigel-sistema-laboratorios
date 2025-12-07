package com.sigel.SigelApi.dto;

import com.sigel.SigelApi.model.Usuario;
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
    private UserDTO usuario;

    public static AuthResponse from(Usuario usuario, String token,
                                    String refreshToken, LocalDateTime expiraEn) {
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenExpira(expiraEn)
                .usuario(UserDTO.fromEntity(usuario)
                ).build();
    }
}