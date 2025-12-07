package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.dto.EquipoInternoDevolucionRequest;
import com.sigel.SigelApi.dto.EquipoInternoPrestadoDTO;
import com.sigel.SigelApi.dto.PrestamoInternoRequest;
import com.sigel.SigelApi.model.Equipo;
import com.sigel.SigelApi.model.Prestamo;
import com.sigel.SigelApi.model.PrestamoDetalle;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PrestamoDetalleImpl {
    PrestamoDetalle buscarDetallePorId(Long prestamoId);

    List<PrestamoDetalle> buscarDetallesPorPrestamo(Long prestamoId);

    List<EquipoInternoPrestadoDTO> buscarDetallesDTOPorPrestamoId(Long prestamoId);

    PrestamoDetalle registrarInspeccionRecibir(
            Prestamo prestamo,
            Equipo equipo,
            PrestamoInternoRequest request,
            MultipartFile fotoAlRecibir
    );

    boolean validarEquipoOcupado(Long equipoId);

    PrestamoDetalle procesarDevolucionEquipo(
            EquipoInternoDevolucionRequest request,
            MultipartFile imagenPosterior
    );

    PrestamoDetalle guardarPrestamoDetalle(PrestamoDetalle prestamoDetalle);
}
