package com.sigel.SigelApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoInternoDevolucionRequest {
    private Long prestamoId;
    private List<EquipoInternoDevolucionRequest> devolucionesEquipo;
}