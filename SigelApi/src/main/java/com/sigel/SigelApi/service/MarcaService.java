package com.sigel.SigelApi.service;

import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import com.sigel.SigelApi.model.Marca;
import com.sigel.SigelApi.repository.MarcaRepository;
import com.sigel.SigelApi.service.implementation.MarcaImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarcaService implements MarcaImpl {
    private final MarcaRepository repository;

    @Override
    public Marca buscarMarcaPorId(Long marcaId) {
        return repository.findById(marcaId)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));
    }
}