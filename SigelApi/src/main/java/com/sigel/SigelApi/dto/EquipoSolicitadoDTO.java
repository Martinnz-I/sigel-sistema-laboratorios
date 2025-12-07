package com.sigel.SigelApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipoSolicitadoDTO {
    private String nombre;
    private String modelo;
    private Long marcaId;
    private int cantidad;
}