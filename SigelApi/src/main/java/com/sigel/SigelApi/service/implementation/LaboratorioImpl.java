package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.dto.LabPickerDTO;
import com.sigel.SigelApi.dto.LaboratorioRequest;
import com.sigel.SigelApi.model.Laboratorio;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LaboratorioImpl {
    List<Laboratorio> obtenerLaboratorios();

    List<LabPickerDTO> obtenerCatalogoLaboratorios();

    Laboratorio buscarPorId(Long id);

    boolean existeLaboratorioPorId(Long laboratorioId);

    Laboratorio actualizar(Long laboratorioId, LaboratorioRequest request);

    Laboratorio guardar(Laboratorio laboratorio);

    void eliminar(Long laboratorioId);

    Laboratorio construirLaboratorio(LaboratorioRequest request, MultipartFile imagen);
}