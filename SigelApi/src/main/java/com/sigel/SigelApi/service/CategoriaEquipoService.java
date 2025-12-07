package com.sigel.SigelApi.service;

import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import com.sigel.SigelApi.model.CategoriaEquipo;
import com.sigel.SigelApi.repository.CategoriaEquipoRepository;
import com.sigel.SigelApi.service.implementation.CategoriaEquipoImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaEquipoService implements CategoriaEquipoImpl {
    private final CategoriaEquipoRepository repository;


    @Override
    public CategoriaEquipo buscarCategoriaPorId(Long categoriaId) {
        return repository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria de Equipo no encontrada"));
    }
}