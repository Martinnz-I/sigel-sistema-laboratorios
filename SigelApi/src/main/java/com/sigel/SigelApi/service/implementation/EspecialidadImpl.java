package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.dto.EspecialidadDTO;
import com.sigel.SigelApi.model.Especialidad;

import java.util.List;

public interface EspecialidadImpl {
    List<EspecialidadDTO> listarEspecialidadesActivas();

    Especialidad buscarPorId(Long id);
}