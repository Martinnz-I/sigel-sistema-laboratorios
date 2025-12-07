package com.sigel.SigelApi.dto;

import com.sigel.SigelApi.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoInternoActivoDTO {
    private Long prestamoId;
    private Long sesionLaboratorioId;
    private String nombreLaboratorio;
    private String materiaActiva;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucion;
    private List<EquipoInternoPrestadoDTO> equipos;
    private LoanStatus estadoGeneral;

    public PrestamoInternoActivoDTO(Long prestamoId, Long sesionLaboratorioId,
                                    String nombreLaboratorio, String materiaActiva,
                                    LocalDateTime fechaPrestamo, LocalDateTime fechaDevolucion,
                                    LoanStatus estadoGeneral) {
        this.prestamoId = prestamoId;
        this.sesionLaboratorioId = sesionLaboratorioId;
        this.nombreLaboratorio = nombreLaboratorio;
        this.materiaActiva = materiaActiva;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.estadoGeneral = estadoGeneral;
    }
}