package com.sigel.SigelApi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificacionEmailRequest {
    @NotBlank(message = "El token es requerido")
    private String token;
}