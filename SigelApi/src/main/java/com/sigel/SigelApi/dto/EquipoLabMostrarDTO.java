package com.sigel.SigelApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipoLabMostrarDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String nombreCategoria;
    private String nombreMarca;
    private String nombreLaboratorio;
    private String fotoEquipoUrl;
    private boolean ocupado;
}