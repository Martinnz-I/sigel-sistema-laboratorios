package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.model.Ubicacion;

public interface UbicacionImpl {
    Ubicacion buscarUbicacionPorId(Long ubicacionId);

    Ubicacion buscarUbicacionPorIdAndLaboratorio(Long ubicacionId, Long laboratorioId);
}