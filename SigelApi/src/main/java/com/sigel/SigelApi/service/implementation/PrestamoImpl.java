package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.dto.PrestamoInternoActivoDTO;
import com.sigel.SigelApi.dto.PrestamoInternoDevolucionRequest;
import com.sigel.SigelApi.dto.PrestamoInternoRequest;
import com.sigel.SigelApi.model.Prestamo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PrestamoImpl {
    List<Prestamo> buscarPrestamosActivosPorSesionLaboratorio(Long sesionLaboratorioId);

    Prestamo buscarPrestamoPorId(Long prestamoId);

    PrestamoInternoActivoDTO obtenerPrestamosInternosActivosPorUsuario();

    Prestamo crearPrestamoInterno(
            PrestamoInternoRequest request,
            MultipartFile fotoPrevia
    );

    void devolverPrestamoInterno(
            PrestamoInternoDevolucionRequest request,
            List<MultipartFile> fotosPosteriores
    );

    Prestamo guardarPrestamo(Prestamo prestamo);
}