package com.sigel.SigelApi.service;

import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import com.sigel.SigelApi.model.Especialidad;
import com.sigel.SigelApi.repository.EspecialidadRepository;
import com.sigel.SigelApi.service.implementation.EspecialidadImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EspecialidadService implements EspecialidadImpl {
    private final EspecialidadRepository repository;

    @Override
    public Especialidad buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
    }
}