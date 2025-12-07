package com.sigel.SigelApi.service;

import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import com.sigel.SigelApi.model.Ubicacion;
import com.sigel.SigelApi.repository.UbicacionRepository;
import com.sigel.SigelApi.service.implementation.UbicacionImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UbicacionService implements UbicacionImpl {
    private final UbicacionRepository repository;
    private final LaboratorioService laboratorioService;

    @Override
    public Ubicacion buscarUbicacionPorId(Long ubicacionId) {
        return repository.findById(ubicacionId).orElseThrow(() -> new ResourceNotFoundException("Ubicacion no encontrada"));
    }

    @Override
    public Ubicacion buscarUbicacionPorIdAndLaboratorio(Long ubicacionId, Long laboratorioId) {
        if(!laboratorioService.existeLaboratorioPorId(laboratorioId)) {
            throw new ResourceNotFoundException("El Laboratorio no existe");
        }

        return repository.findByIdAndLaboratorioId(ubicacionId, laboratorioId)
                .orElseThrow(() -> new ResourceNotFoundException("¡La ubicación no pertenece a ese laboratorio!"));
    }
}