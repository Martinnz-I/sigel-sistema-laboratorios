package com.sigel.SigelApi.service;

import com.sigel.SigelApi.dto.EspecialidadDTO;
import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import com.sigel.SigelApi.model.Especialidad;
import com.sigel.SigelApi.repository.EspecialidadRepository;
import com.sigel.SigelApi.service.implementation.EspecialidadImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialidadService implements EspecialidadImpl {
    private final EspecialidadRepository repository;

    @Override
    public List<EspecialidadDTO> listarEspecialidadesActivas() {
        return repository.findByActivoTrue().stream().map(especialidad -> EspecialidadDTO.builder()
                .id(especialidad.getId())
                .nombre(especialidad.getNombre())
                .abreviatura(especialidad.getAbreviatura())
                .build()).toList();
    }

    @Override
    public Especialidad buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
    }
}