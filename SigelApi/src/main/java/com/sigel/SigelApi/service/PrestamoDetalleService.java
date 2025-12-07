package com.sigel.SigelApi.service;

import com.sigel.SigelApi.dto.EquipoInternoDevolucionRequest;
import com.sigel.SigelApi.dto.EquipoInternoPrestadoDTO;
import com.sigel.SigelApi.dto.PrestamoInternoRequest;
import com.sigel.SigelApi.enums.LoanStatus;
import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import com.sigel.SigelApi.model.Equipo;
import com.sigel.SigelApi.model.Prestamo;
import com.sigel.SigelApi.model.PrestamoDetalle;
import com.sigel.SigelApi.repository.PrestamoDetalleRepository;
import com.sigel.SigelApi.service.implementation.PrestamoDetalleImpl;
import com.sigel.SigelApi.util.ChecklistConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoDetalleService implements PrestamoDetalleImpl {

    private final PrestamoDetalleRepository repository;
    private final StorageService storageService;
    private final ChecklistConverter checklistConverter;

    @Override
    public PrestamoDetalle buscarDetallePorId(Long prestamoId) {
        return repository.findById(prestamoId).orElseThrow(() ->
                new ResourceNotFoundException("No ha sido encontrado ese equipo en prestamo"));
    }

    @Override
    public List<PrestamoDetalle> buscarDetallesPorPrestamo(Long prestamoId) {
        return repository.findByPrestamoId(prestamoId);
    }

    @Override
    public List<EquipoInternoPrestadoDTO> buscarDetallesDTOPorPrestamoId(Long prestamoId) {
        List<PrestamoDetalle> listaDetalles = repository.findByPrestamoId(prestamoId);

        return listaDetalles.stream().map(this::convertirEquipoInternoDTO).toList();
    }

    @Override
    public PrestamoDetalle registrarInspeccionRecibir(
            Prestamo prestamo,
            Equipo equipo,
            PrestamoInternoRequest request,
            MultipartFile fotoAlrecibir
    ) {
        PrestamoDetalle prestamoDetalle = PrestamoDetalle.builder()
                .prestamo(prestamo)
                .equipo(equipo)
                .estadoAlRecibir(request.getEstadoAlRecibir())
                .fotoAlRecibirUrl(storageService.subirImagen(fotoAlrecibir))
                .observacionesAlRecibir(request.getObservacionesAlRecibir())
                .checklistRecibir(checklistConverter.toMap(request.getChecklistRecibir()))
                .build();

        return repository.save(prestamoDetalle);
    }

    @Override
    public boolean validarEquipoOcupado(Long equipoId) {
        return repository.existsByEquipoIdAndPrestamoEstado(
                equipoId,
                LoanStatus.ACTIVO
        );
    }

    @Override
    public PrestamoDetalle procesarDevolucionEquipo(EquipoInternoDevolucionRequest request, MultipartFile imagenPosterior) {
        PrestamoDetalle detalle = this.buscarDetallePorId(request.getDetalleId());

        storageService.validarImagen(imagenPosterior);

        LocalDateTime ahora = LocalDateTime.now();

        detalle.setEstadoAlDevolver(request.getEstadoAlDevolver());
        detalle.setFotoAlDevolverUrl(storageService.subirImagen(imagenPosterior));
        detalle.setObservacionesAlDevolver(request.getObservacionesAlDevolver());
        detalle.setChecklistDevolver(checklistConverter.toMap(request.getChecklistDevolver()));
        detalle.setFuncionoCorrectamente(request.getFuncionoCorrectamente());
        detalle.setFechaInspeccionDevolver(ahora);
        detalle.setDevuelto(true);
        detalle.setFechaDevolucion(ahora);

        return repository.save(detalle);
    }

    @Override
    public PrestamoDetalle guardarPrestamoDetalle(PrestamoDetalle prestamoDetalle) {
        return repository.save(prestamoDetalle);
    }

    private EquipoInternoPrestadoDTO convertirEquipoInternoDTO(PrestamoDetalle prestamoDetalle) {
        Equipo equipo = prestamoDetalle.getEquipo();
        return EquipoInternoPrestadoDTO.builder()
                .detalleId(prestamoDetalle.getId())
                .equipoId(equipo.getId())
                .nombreEquipo(equipo.getNombre())
                .codigoEquipo(equipo.getCodigo())
                .fotoEquipoUrl(equipo.getFotos().values().iterator().next())
                .nombreCategoria(equipo.getCategoria().getNombre())
                .nombreMarca(equipo.getMarca().getNombre())
                .estadoAlRecibir(prestamoDetalle.getEstadoAlRecibir())
                .build();
    }
}