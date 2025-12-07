package com.sigel.SigelApi.dto;

import com.sigel.SigelApi.enums.TipoPrestamo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEquipoRequest {
    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "El ID de categoría debe ser un número positivo")
    private Long categoriaId;

    @NotNull(message = "La marca es obligatoria")
    @Positive(message = "El ID de marca debe ser un número positivo")
    private Long marcaId;

    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    private String modelo;

    @Size(max = 100, message = "El número de serie no puede exceder 100 caracteres")
    private String numeroSerie;

    @NotNull(message = "El laboratorio es obligatorio")
    @Positive(message = "El ID de laboratorio debe ser un número positivo")
    private Long laboratorioId;

    @Positive(message = "El ID de ubicación debe ser un número positivo")
    private Long ubicacionId;

    private Map<String, Object> especificaciones;

    @NotNull(message = "Debe indicar si está disponible para préstamo")
    private Boolean disponiblePrestamo;

    @NotNull(message = "El tipo de préstamo es obligatorio")
    private TipoPrestamo tipoPrestamo;

    @NotBlank(message = "El estado físico es obligatorio")
    private String estadoFisico;

    private String notas;
}