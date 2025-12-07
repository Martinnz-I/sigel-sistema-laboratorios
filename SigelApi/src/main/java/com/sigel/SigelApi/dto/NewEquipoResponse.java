package com.sigel.SigelApi.dto;

import com.sigel.SigelApi.model.Equipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEquipoResponse {
    private String codigo;
    private String codigoQr;
    private String nombre;
    private String nombreLaboratorio;
    private String nombreCategoria;
    private String nombreMarca;
    private String modelo;

    public static NewEquipoResponse fromEntity(Equipo equipo) {
        return NewEquipoResponse.builder()
                .codigo(equipo.getCodigo())
                .codigoQr(equipo.getCodigoQr())
                .nombre(equipo.getNombre())
                .nombreLaboratorio(equipo.getLaboratorio().getNombre())
                .nombreCategoria(equipo.getCategoria().getNombre())
                .nombreMarca(equipo.getMarca().getNombre())
                .modelo(equipo.getModelo())
                .build();
    }
}