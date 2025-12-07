package com.sigel.SigelApi.service;

import com.sigel.SigelApi.dto.GrupoDTO;
import com.sigel.SigelApi.exceptions.AuthenticationException;
import com.sigel.SigelApi.model.Grupo;
import com.sigel.SigelApi.repository.GrupoRepository;
import com.sigel.SigelApi.service.implementation.GrupoImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrupoService implements GrupoImpl {
    private final GrupoRepository repository;

    @Override
    public List<Grupo> listar() {
        return repository.findAll();
    }

    @Override
    public List<GrupoDTO> listarGruposActivos() {
        return repository.findByActivoTrue().stream().map(grupo -> GrupoDTO.builder()
                        .id(grupo.getId())
                        .semestre(grupo.getSemestre())
                        .grupo(grupo.getGrupo())
                        .especialidadId(grupo.getEspecialidad().getId())
                        .build()).toList();
    }

    @Override
    public List<Grupo> listarPorSemestre(Integer semestre) {
        return repository.findBySemestre(semestre);
    }

    @Override
    public List<Grupo> listarPorEspecialidad(Long especialidadId) {
        return repository.findByEspecialidadId(especialidadId);
    }

    @Override
    public Grupo buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new AuthenticationException("Grupo no encontrado"));
    }
}