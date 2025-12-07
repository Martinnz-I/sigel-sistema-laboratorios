package com.sigel.SigelApi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sigel.SigelApi.enums.EstadoSesionLab;
import com.sigel.SigelApi.enums.ModoAsignacionLab;
import com.sigel.SigelApi.model.SesionLaboratorio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionLabResponse {
    private Long id;
    private String codigo;

    private Long laboratorioId;
    private String laboratorioNombre;
    private String laboratorioCodigo;

    private Long maestroId;
    private String maestroNombre;

    private Long grupoId;
    private String grupoNombre;

    private String materia;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaFinEstimada;

    private EstadoSesionLab estado;
    private ModoAsignacionLab modoAsignacion;
    private Boolean permiteCambioEquipo;
    private String notas;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static SesionLabResponse fromEntity(SesionLaboratorio sesion) {
        return SesionLabResponse.builder()
                .id(sesion.getId())
                .codigo(sesion.getCodigo())
                .laboratorioId(sesion.getLaboratorio().getId())
                .laboratorioNombre(sesion.getLaboratorio().getNombre())
                .laboratorioCodigo(sesion.getLaboratorio().getCodigo())
                .maestroId(sesion.getMaestro().getId())
                .maestroNombre(sesion.getMaestro().getNombre() + " " +
                        sesion.getMaestro().getApellidoPaterno())
                .grupoId(sesion.getGrupo() != null ? sesion.getGrupo().getId() : null)
                .grupoNombre(sesion.getGrupo() != null ?
                        sesion.getGrupo().getSemestre() + "°" + sesion.getGrupo().getGrupo() :
                        null)
                .materia(sesion.getMateria())
                .fechaInicio(sesion.getFechaInicio())
                .fechaFinEstimada(sesion.getFechaFinEstimada())
                .estado(sesion.getEstado())
                .modoAsignacion(sesion.getModoAsignacion())
                .permiteCambioEquipo(sesion.isPermiteCambioEquipo())
                .notas(sesion.getNotas())
                .createdAt(sesion.getCreatedAt())
                .build();
    }
}