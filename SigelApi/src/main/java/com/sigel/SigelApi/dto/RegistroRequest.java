package com.sigel.SigelApi.dto;

import com.sigel.SigelApi.enums.UserRole;
import com.sigel.SigelApi.validation.PasswordMatch;
import com.sigel.SigelApi.validation.PasswordStrength;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PasswordMatch
public class RegistroRequest {
    @Pattern(regexp = "^[0-9]{6,20}$", message = "Matrícula: solo números (6-20 dígitos)")
    private String matricula;

    @Email(message = "Email debe ser válido")
    @NotBlank(message = "Email es requerido")
    private String email;

    @NotBlank(message = "Contraseña es requerida")
    @Size(min = 8, message = "Contraseña debe tener al menos 8 caracteres")
    @PasswordStrength
    private String password;

    @NotBlank(message = "Confirmación de contraseña es requerida")
    private String confirmarPassword;

    @NotBlank(message = "Nombre es requerido")
    @Size(min = 2, max = 100, message = "Nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "Apellido paterno es requerido")
    @Size(min = 2, max = 100, message = "Apellido debe tener entre 2 y 100 caracteres")
    private String apellidoPaterno;

    @NotBlank(message = "Apellido materno es requerido")
    @Size(min = 2, max = 100, message = "Apellido debe tener entre 2 y 100 caracteres")
    private String apellidoMaterno;

    private Long grupoId;

    private String claveDocente;

    @NotNull(message = "El rol del usuario es requerido")
    private UserRole rol;
}