package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.dto.EquipoLabMostrarDTO;
import com.sigel.SigelApi.dto.EstudianteDTO;
import com.sigel.SigelApi.dto.SesionLabRequest;
import com.sigel.SigelApi.model.SesionLaboratorio;

import java.util.List;
import java.util.Optional;

public interface SesionLaboratorioImpl {

    Optional<SesionLaboratorio> buscarSesionActivaPorLaboratorioId(Long laboratorioId);

    SesionLaboratorio buscarSesionActivaPorGrupoId(Long grupoId);

    SesionLaboratorio nuevaSesionLaboratorio(SesionLabRequest request);

    List<EstudianteDTO> obtenerEstudiantesPorSesion(Long sesionLaboratorioId);

    List<EquipoLabMostrarDTO> obtenerEquiposPorSesion(Long sesionLaboratorioId);

    int autoFinalizarSesionesVencidas();


}