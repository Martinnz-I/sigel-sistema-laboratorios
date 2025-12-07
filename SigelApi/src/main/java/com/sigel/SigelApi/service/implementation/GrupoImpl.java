package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.dto.GrupoDTO;
import com.sigel.SigelApi.model.Grupo;

import java.util.List;

public interface GrupoImpl {
    List<Grupo> listar();

    List<GrupoDTO> listarGruposActivos();

    List<Grupo> listarPorSemestre(Integer semestre);

    List<Grupo> listarPorEspecialidad(Long especialidadId);

    Grupo buscarPorId(Long id);


}