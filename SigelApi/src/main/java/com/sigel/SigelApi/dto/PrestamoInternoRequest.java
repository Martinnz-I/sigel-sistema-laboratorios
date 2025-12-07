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
public class PrestamoInternoRequest {
    private Long equipoId;
    private EstadoEquipoPrestamo estadoAlRecibir;
    private String observacionesAlRecibir;
    private ChecklistInspeccionDTO checklistRecibir;
}