package com.sigel.SigelApi.service;

import com.sigel.SigelApi.dto.EquipoInternoDevolucionRequest;
import com.sigel.SigelApi.dto.PrestamoInternoActivoDTO;
import com.sigel.SigelApi.dto.PrestamoInternoDevolucionRequest;
import com.sigel.SigelApi.dto.PrestamoInternoRequest;
import com.sigel.SigelApi.enums.EquipmentStatus;
import com.sigel.SigelApi.enums.EstadoSesionLab;
import com.sigel.SigelApi.enums.LoanStatus;
import com.sigel.SigelApi.enums.TipoPrestamo;
import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import com.sigel.SigelApi.exceptions.UnauthorizedException;
import com.sigel.SigelApi.repository.PrestamoRepository;
import com.sigel.SigelApi.repository.SesionLaboratorioRepository;
import com.sigel.SigelApi.security.JwtUtil;
import com.sigel.SigelApi.service.implementation.PrestamoImpl;
import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.model.Equipo;
import com.sigel.SigelApi.model.Prestamo;
import com.sigel.SigelApi.model.PrestamoDetalle;
import com.sigel.SigelApi.model.SesionLaboratorio;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PrestamoService implements PrestamoImpl {
    private final PrestamoRepository repository;
    private final ValidacionPrestamoService validacionPrestamoService;
    private final EquipoService equipoService;
    private final SesionLaboratorioRepository sesionLaboratorioRepository;
    private final PrestamoDetalleService prestamoDetalleService;
    private final StorageService storageService;
    private final JwtUtil jwtUtil;

    @Override
    public List<Prestamo> buscarPrestamosActivosPorSesionLaboratorio(Long sesionLaboratorioId) {
        return repository.findBySesionLaboratorioIdAndEstado(
                sesionLaboratorioId,
                LoanStatus.ACTIVO
        );
    }

    @Override
    public Prestamo buscarPrestamoPorId(Long prestamoId) {
        return repository.findById(prestamoId).orElseThrow(() ->
                new ResourceNotFoundException("Prestamo no encontrado"));
    }

    @Override
    public PrestamoInternoActivoDTO obtenerPrestamosInternosActivosPorUsuario() {
        Usuario usuario = jwtUtil.obtenerUsuarioToken();

        if(usuario == null) {
            throw new UnauthorizedException("Usuario no autenticado");
        }

        if (usuario.getGrupo() == null) {
            return null;
        }

        SesionLaboratorio sesionLaboratorio = sesionLaboratorioRepository.findByGrupoIdAndEstado(
                usuario.getGrupo().getId(),
                EstadoSesionLab.ACTIVA
        ).orElse(null);

        if(sesionLaboratorio == null) {
            return null;
        }

        PrestamoInternoActivoDTO prestamo = repository.findPrestamoActivoDTOBySesionAndUsuario(
                sesionLaboratorio.getId(),
                usuario.getId()
        ).orElse(null);

        if(prestamo == null) {
            return null;
        }

        prestamo.setEquipos(prestamoDetalleService.buscarDetallesDTOPorPrestamoId(prestamo.getPrestamoId()));

        return prestamo;
    }

    @Override
    @Transactional
    public Prestamo crearPrestamoInterno(PrestamoInternoRequest request, MultipartFile fotoPrevia) {
        Equipo equipo = equipoService.buscarEquipoPorId(request.getEquipoId());

        SesionLaboratorio sesion = validacionPrestamoService.validarEquipoYObtenerSesion(equipo);

        validacionPrestamoService.validarDisponibilidadEquipo(equipo);

        storageService.validarImagen(fotoPrevia);

        Usuario usuario = validacionPrestamoService.validarUsuarioParaPrestamoInterno(sesion.getGrupo());

        Prestamo prestamoExistente = repository.findBySesionLaboratorioIdAndUsuarioIdAndEstado(
                sesion.getId(),
                usuario.getId(),
                LoanStatus.ACTIVO
        );

        boolean esPrestamoNuevo = (prestamoExistente == null);

        Prestamo prestamo = esPrestamoNuevo
                ? crearNuevoPrestamoInterno(sesion, usuario)
                : prestamoExistente;

        System.out.println("Prestamo: " + prestamo);

        prestamoDetalleService.registrarInspeccionRecibir(
                prestamo,
                equipo,
                request,
                fotoPrevia
        );

        actualizarEstadoEquipo(equipo, EquipmentStatus.EN_USO);

        return prestamo;
    }

    @Override
    public void devolverPrestamoInterno(PrestamoInternoDevolucionRequest request, List<MultipartFile> fotosPosteriores) {
        Prestamo prestamo = buscarPrestamoPorId(request.getPrestamoId());

        prestamo.setEstado(LoanStatus.COMPLETADO);

        LocalDateTime ahora = LocalDateTime.now();

        if(ahora.isAfter(prestamo.getFechaDevolucionEstimada())) {
            prestamo.setATiempo(false);
            prestamo.setMinutosRetraso((int) Duration.between(prestamo.getFechaDevolucionEstimada(), ahora).toMinutes());
        } else {
            prestamo.setATiempo(true);
        }

        repository.save(prestamo);

        IntStream.range(0, request.getDevolucionesEquipo().size())
                .forEach(i -> {
                    EquipoInternoDevolucionRequest devolucion = request.getDevolucionesEquipo().get(i);
                    MultipartFile imagenPosterior = fotosPosteriores.get(i);

                    PrestamoDetalle detalle = prestamoDetalleService.procesarDevolucionEquipo(
                            devolucion,
                            imagenPosterior
                    );

                    actualizarEstadoEquipo(detalle.getEquipo(), EquipmentStatus.DISPONIBLE);
                });
    }

    @Override
    public Prestamo guardarPrestamo(Prestamo prestamo) {
        return repository.save(prestamo);
    }

    private String generarCodigoPrestamo(String matricula) {
        String subMatricula = matricula.substring(matricula.length() - 4);
        return String.format("PRE-%s-%03d", subMatricula, (int)(Math.random() * 900 + 100));
    }

    private Prestamo crearNuevoPrestamoInterno(SesionLaboratorio sesion, Usuario usuario) {
        Usuario encargado = sesion.getLaboratorio().getEncargado();

        Prestamo nuevoPrestamo = Prestamo.builder()
                .codigo(generarCodigoPrestamo(usuario.getMatricula()))
                .usuario(usuario)
                .autorizador(encargado)
                .maestroResponsable(encargado)
                .sesionLaboratorio(sesion)
                .tipoPrestamo(TipoPrestamo.INTERNO)
                .motivo("Utilizar el equipo para realizar prácticas dentro del laboratorio")
                .materia(sesion.getMateria())
                .fechaDevolucionEstimada(sesion.getFechaFinEstimada())
                .build();

        return repository.save(nuevoPrestamo);
    }



    private void actualizarEstadoEquipo(Equipo equipo, EquipmentStatus estado) {
        equipo.setEstado(estado);
        equipoService.guardarEquipo(equipo);
    }
}