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
public class EquipoInternoDevolucionRequest {
    private Long detalleId;
    private Long equipoId;
    private EstadoEquipoPrestamo estadoAlDevolver;
    private String observacionesAlDevolver;
    private ChecklistInspeccionDTO checklistDevolver;
    private Boolean funcionoCorrectamente;
}