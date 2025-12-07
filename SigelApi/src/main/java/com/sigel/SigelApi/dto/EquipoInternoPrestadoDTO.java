package com.sigel.SigelApi.dto;

import com.sigel.SigelApi.enums.EstadoEquipoPrestamo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipoInternoPrestadoDTO {
    private Long detalleId;
    private Long equipoId;
    private String nombreEquipo;
    private String codigoEquipo;
    private String fotoEquipoUrl;
    private String nombreCategoria;
    private String nombreMarca;
    private EstadoEquipoPrestamo estadoAlRecibir;
}