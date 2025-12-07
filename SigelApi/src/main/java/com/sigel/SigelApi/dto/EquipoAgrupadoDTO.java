package com.sigel.SigelApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoAgrupadoDTO {
    private String nombre;
    private Long categoriaId;
    private String categoriaNombre;
    private Long marcaId;
    private String marcaNombre;
    private String modelo;
    private Long especialidadId;
    private String nombreEspecialidad;
    private Long cantidadDisponible;
}