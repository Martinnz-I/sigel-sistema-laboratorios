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
public class EquipoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String nombreCategoria;
    private String nombreMarca;
    private String nombreLaboratorio;
    private String fotoEquipoUrl;

    public static EquipoDTO fromEntity(Equipo equipo) {
        return EquipoDTO.builder()
                .id(equipo.getId())
                .codigo(equipo.getCodigo())
                .nombre(equipo.getNombre())
                .nombreCategoria(equipo.getCategoria().getNombre())
                .nombreMarca(equipo.getMarca().getNombre())
                .nombreLaboratorio(equipo.getLaboratorio().getNombre())
                .fotoEquipoUrl(equipo.getFotos().values().iterator().next())
                .build();
    }
}