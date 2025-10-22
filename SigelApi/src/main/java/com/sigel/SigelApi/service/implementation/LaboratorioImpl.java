package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.dto.LaboratorioRequest;
import com.sigel.SigelApi.model.Laboratorio;

import java.util.List;

public interface LaboratorioImpl {
    List<Laboratorio> buscarTodos();

    Laboratorio buscarPorId(Long id);

    Laboratorio actualizar(Long laboratorioId, LaboratorioRequest request);

    Laboratorio guardar(Laboratorio laboratorio);

    void eliminar(Long laboratorioId);

    Laboratorio construirLaboratorio(LaboratorioRequest request);
}