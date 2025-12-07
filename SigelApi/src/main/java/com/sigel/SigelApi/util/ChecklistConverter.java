package com.sigel.SigelApi.util;

import com.sigel.SigelApi.dto.ChecklistInspeccionDTO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChecklistConverter {
    public Map<String, Object> toMap(ChecklistInspeccionDTO dto) {
        if (dto == null) {
            return new HashMap<>();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("limpieza", dto.getLimpieza());
        map.put("integridadFisica", dto.getIntegridadFisica());
        map.put("accesoriosCompletos", dto.getAccesoriosCompletos());
        map.put("funcionalidad", dto.getFuncionalidad());
        map.put("cablesConectores", dto.getCablesConectores());
        map.put("etiquetasLegibles", dto.getEtiquetasLegibles());

        return map;
    }

    public ChecklistInspeccionDTO toDTO(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        return ChecklistInspeccionDTO.builder()
                .limpieza((String) map.get("limpieza"))
                .integridadFisica((String) map.get("integridadFisica"))
                .accesoriosCompletos((Boolean) map.get("accesoriosCompletos"))
                .funcionalidad((String) map.get("funcionalidad"))
                .cablesConectores((String) map.get("cablesConectores"))
                .etiquetasLegibles((Boolean) map.get("etiquetasLegibles"))
                .build();
    }
}