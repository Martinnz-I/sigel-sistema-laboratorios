package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.dto.EquipoAgrupadoDTO;
import com.sigel.SigelApi.dto.EquipoDTO;
import com.sigel.SigelApi.dto.NewEquipoRequest;
import com.sigel.SigelApi.model.Equipo;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface EquipoImpl {
    List<EquipoAgrupadoDTO> obtenerCatalogoEquipos();

    List<Equipo> listarEquiposPorLaboratorioId(Long laboratorioId);

    Equipo buscarEquipoPorCodigo(String codigo);

    Equipo buscarEquipoPorId(Long idEquipo);

    EquipoDTO buscarEquipoPorIdDTO(Long idEquipo);

    Equipo registrarEquipo(NewEquipoRequest request, List<MultipartFile> imagenes);

    Long validarEquipoASolicitar(String codigoEquipo);

    Equipo guardarEquipo(Equipo equipo);
}