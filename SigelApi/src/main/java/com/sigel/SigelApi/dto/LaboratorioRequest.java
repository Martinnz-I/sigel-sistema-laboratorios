package com.sigel.SigelApi.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboratorioRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @Size(max = 20, message = "El código no puede tener más de 20 caracteres")
    private String codigo;

    @NotNull(message = "La coordenada X es obligatoria")
    @DecimalMin(value = "0.0", inclusive = true, message = "La coordenada X debe ser positiva")
    private BigDecimal coordenadaX;

    @NotNull(message = "La coordenada Y es obligatoria")
    @DecimalMin(value = "0.0", inclusive = true, message = "La coordenada Y debe ser positiva")
    private BigDecimal coordenadaY;

    @NotNull(message = "El piso es obligatorio")
    @Min(value = 1, message = "El piso debe ser 1 o mayor")
    private Integer piso;

    @Max(value = 50, message = "La capacidad debe ser menor o igual a 50")
    private Integer capacidadAlumnos;

    @NotNull(message = "La especialidad es obligatoria")
    private Long especialidadId;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    @NotNull(message = "El horario de apertura es obligatorio")
    private LocalTime horarioApertura;

    @NotNull(message = "El horario de cierre es obligatorio")
    private LocalTime horarioCierre;
}