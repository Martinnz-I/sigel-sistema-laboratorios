package com.sigel.SigelApi.dto;

import com.sigel.SigelApi.enums.ModoAsignacionLab;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionLabRequest {
    private Long laboratorioId;
    private Long grupoId;
    private String materia;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinEstimada;
    private ModoAsignacionLab modoAsignacion;
    private Boolean permiteCambioEquipo;
    private String notas;
}