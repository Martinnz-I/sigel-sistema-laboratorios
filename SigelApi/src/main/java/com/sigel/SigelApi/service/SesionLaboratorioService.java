package com.sigel.SigelApi.service;

import com.sigel.SigelApi.dto.EquipoLabMostrarDTO;
import com.sigel.SigelApi.dto.EstudianteDTO;
import com.sigel.SigelApi.dto.SesionLabRequest;
import com.sigel.SigelApi.enums.EquipmentStatus;
import com.sigel.SigelApi.enums.EstadoSesionLab;
import com.sigel.SigelApi.exceptions.BadRequestException;
import com.sigel.SigelApi.model.*;
import com.sigel.SigelApi.repository.SesionLaboratorioRepository;
import com.sigel.SigelApi.security.JwtUtil;
import com.sigel.SigelApi.service.implementation.SesionLaboratorioImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SesionLaboratorioService implements SesionLaboratorioImpl {
    private final SesionLaboratorioRepository repository;
    private final UsuarioService usuarioService;
    private final PrestamoService prestamoService;
    private final LaboratorioService laboratorioService;
    private final EquipoService equipoService;
    private final FinalizacionSesionService finalizacionSesionService;
    private final GrupoService grupoService;
    private final JwtUtil jwtUtil;

    @Override
    public Optional<SesionLaboratorio> buscarSesionActivaPorLaboratorioId(Long laboratorioId) {
        return repository.findByLaboratorioIdAndEstado(
                laboratorioId,
                EstadoSesionLab.ACTIVA
        );
    }

    @Override
    public SesionLaboratorio buscarSesionActivaPorGrupoId(Long grupoId) {
        return repository.findByGrupoIdAndEstado(grupoId, EstadoSesionLab.ACTIVA).orElse(null);
    }

    @Override
    public SesionLaboratorio nuevaSesionLaboratorio(SesionLabRequest request) {
        Laboratorio laboratorio = laboratorioService.buscarPorId(request.getLaboratorioId());
        Usuario usuarioActual = jwtUtil.obtenerUsuarioToken();

        validarSesionLaboratorio(request, laboratorio, usuarioActual);

        return repository.save(construirLaboratorio(request, laboratorio, usuarioActual));
    }

    @Override
    public List<EstudianteDTO> obtenerEstudiantesPorSesion(Long sesionLaboratorioId) {
        SesionLaboratorio sesion = repository.findById(sesionLaboratorioId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        List<Usuario> estudiantes = usuarioService.buscarTodosLosAlumnosPorGrupoId(
                sesion.getGrupo().getId()
        );

        System.out.println("Estudiantes del grupo: " + estudiantes);

        List<Prestamo> prestamos = prestamoService.buscarPrestamosActivosPorSesionLaboratorio(
                sesion.getId()
        );

        Map<Long, List<Long>> equiposPorEstudiante = new HashMap<>();
        Map<Long, LocalDateTime> fechaConexionPorEstudiante = new HashMap<>();

        for (Prestamo prestamo : prestamos) {
            Long usuarioId = prestamo.getUsuario().getId();

            // Obtener IDs de equipos no devueltos
            List<Long> equiposIds = prestamo.getDetalles().stream()
                    .filter(detalle -> !detalle.isDevuelto())
                    .map(detalle -> detalle.getEquipo().getId()).toList();

            equiposPorEstudiante.put(usuarioId, equiposIds);

            fechaConexionPorEstudiante.put(usuarioId, prestamo.getFechaSolicitud());
        }

        return estudiantes.stream().map(estudiante -> mapToEstudianteDTO(
                estudiante,
                equiposPorEstudiante.getOrDefault(estudiante.getId(), Collections.emptyList()),
                fechaConexionPorEstudiante.get(estudiante.getId())
        )).toList();
    }

    @Override
    public List<EquipoLabMostrarDTO> obtenerEquiposPorSesion(Long sesionLaboratorioId) {
        SesionLaboratorio sesion = repository.findById(sesionLaboratorioId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        List<Equipo> equipos = equipoService.listarEquiposPorLaboratorioId(sesion.getLaboratorio().getId());

        return equipos.stream().map(this::mapToEquipoLabMostrarDTO).toList();
    }

    @Override
    public int autoFinalizarSesionesVencidas() {
        LocalDateTime limiteGracia = LocalDateTime.now().minusMinutes(30);

        List<SesionLaboratorio> sesionesVencidas = repository.findSesionesActivasVencidas(limiteGracia);

        if(sesionesVencidas.isEmpty()) {
            return 0;
        }

        for(SesionLaboratorio sesion : sesionesVencidas) {
            finalizacionSesionService.finalizarSesionConPrestamos(sesion);
        }

        return sesionesVencidas.size();
    }

    private SesionLaboratorio construirLaboratorio(SesionLabRequest request, Laboratorio laboratorio, Usuario usuario) {
        Grupo grupo = request.getGrupoId() != null ? grupoService.buscarPorId(request.getGrupoId()) : null;

        return SesionLaboratorio.builder()
                .codigo(generarCodigoSesion(laboratorio, request.getFechaInicio()))
                .laboratorio(laboratorio)
                .maestro(usuario)
                .grupo(grupo)
                .materia(request.getMateria())
                .fechaInicio(request.getFechaInicio())
                .fechaFinEstimada(request.getFechaFinEstimada())
                .estado(EstadoSesionLab.ACTIVA)
                .modoAsignacion(request.getModoAsignacion())
                .permiteCambioEquipo(request.getPermiteCambioEquipo())
                .notas(request.getNotas())
                .build();
    }

    private String generarCodigoSesion(Laboratorio laboratorio, LocalDateTime fechaInicio) {
        String fecha = fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String labCodigo = laboratorio.getCodigo()
                .replace("LAB-", "")
                .replace("-", "");

        Integer secuencial = repository.countByLaboratorioCodigoAndFechaInicioBetween(
                laboratorio.getCodigo(),
                fechaInicio.toLocalDate().atStartOfDay(),
                fechaInicio.toLocalDate().atTime(23,59,59)
        ) + 1;

        String secuencialFormateado = String.format("%03d", secuencial);

        return String.format("SES-%s-%s-%s", labCodigo, fecha, secuencialFormateado);
    }

    private void validarSesionLaboratorio(SesionLabRequest request, Laboratorio laboratorio, Usuario usuario) {
        if(repository.existsByMaestroIdAndEstado(usuario.getId(), EstadoSesionLab.ACTIVA)) {
            throw new BadRequestException("Usted ya tiene una sesión de laboratorio activa");
        }

        SesionLaboratorio sesionOcupada = repository.findByLaboratorioIdAndEstado(
                request.getLaboratorioId(),
                EstadoSesionLab.ACTIVA
        ).orElse(null);

        if(sesionOcupada != null) {
            throw new BadRequestException(
                    String.format("Imposible iniciar una sesión en el laboratorio: %s. Esta siendo ocupado por el Profesor: %s %s.",
                            laboratorio.getNombre(),
                            sesionOcupada.getMaestro().getNombre(),
                            sesionOcupada.getMaestro().getApellidoPaterno())
            );
        }
    }

    private EstudianteDTO mapToEstudianteDTO(Usuario usuario, List<Long> equiposIds, LocalDateTime conectadoEl) {
        return EstudianteDTO.builder()
                .id(usuario.getId())
                .matricula(usuario.getMatricula())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellidoPaterno(usuario.getApellidoPaterno())
                .apellidoMaterno(usuario.getApellidoMaterno())
                .fotoPerfilUrl(usuario.getFotoPerfilUrl())
                .grupo(usuario.getGrupo().getGrupo())
                .semestre(usuario.getGrupo().getSemestre())
                .especialidad(usuario.getGrupo().getEspecialidad().getNombre())
                .equipoId(equiposIds)
                .conectado(equiposIds != null)
                .conectadoEl(conectadoEl)
                .build();
    }

    private EquipoLabMostrarDTO mapToEquipoLabMostrarDTO(Equipo equipo) {
        return EquipoLabMostrarDTO.builder()
                .id(equipo.getId())
                .codigo(equipo.getCodigo())
                .nombre(equipo.getNombre())
                .nombreCategoria(equipo.getCategoria().getNombre())
                .nombreLaboratorio(equipo.getLaboratorio().getNombre())
                .fotoEquipoUrl(equipo.getFotos().values().iterator().next())
                .ocupado(equipo.getEstado() == EquipmentStatus.EN_USO)
                .build();
    }
}