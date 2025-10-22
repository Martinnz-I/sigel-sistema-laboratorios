package com.sigel.SigelApi.service;

import com.sigel.SigelApi.dto.LaboratorioRequest;
import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import com.sigel.SigelApi.model.Especialidad;
import com.sigel.SigelApi.model.Laboratorio;
import com.sigel.SigelApi.repository.LaboratorioRepository;
import com.sigel.SigelApi.service.implementation.LaboratorioImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LaboratorioService implements LaboratorioImpl {
    private final LaboratorioRepository repository;
    private final EspecialidadService especialidadService;
    private final UsuarioService usuarioService;

    @Override
    public List<Laboratorio> buscarTodos() {
        return repository.findAll();
    }

    @Override
    public Laboratorio buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Laboratorio no encontrado"));
    }

    @Transactional
    @Override
    public Laboratorio actualizar(Long laboratorioId, LaboratorioRequest request) {
        Laboratorio laboratorio = this.buscarPorId(laboratorioId);

        laboratorio.setNombre(request.getNombre());
        laboratorio.setCodigo(request.getCodigo() != null ? request.getCodigo() : this.generarCodigo(request.getEspecialidadId()));
        laboratorio.setCoordenadaX(request.getCoordenadaX());
        laboratorio.setCoordenadaY(request.getCoordenadaY());
        laboratorio.setPiso(request.getPiso());
        laboratorio.setCapacidadAlumnos(request.getCapacidadAlumnos());
        laboratorio.setDescripcion(request.getDescripcion());
        laboratorio.setHorarioApertura(request.getHorarioApertura());
        laboratorio.setHorarioCierre(request.getHorarioCierre());

        if (!Objects.equals(laboratorio.getEspecialidad().getId(), request.getEspecialidadId())) {
            laboratorio.setEspecialidad(especialidadService.buscarPorId(request.getEspecialidadId()));
        }

        return laboratorio;
    }

    @Override
    public Laboratorio guardar(Laboratorio laboratorio) {
        return repository.save(laboratorio);
    }

    @Override
    public void eliminar(Long laboratorioId) {
        Laboratorio laboratorio = this.buscarPorId(laboratorioId);
        repository.delete(laboratorio);
    }

    @Override
    public Laboratorio construirLaboratorio(LaboratorioRequest request) {
        Laboratorio laboratorio = Laboratorio.builder()
                .codigo(request.getCodigo() != null ? request.getCodigo() : generarCodigo(request.getEspecialidadId()))
                .nombre(request.getNombre())
                .coordenadaX(request.getCoordenadaX())
                .coordenadaY(request.getCoordenadaY())
                .piso(request.getPiso())
                .capacidadAlumnos(request.getCapacidadAlumnos())
                .especialidad(especialidadService.buscarPorId(request.getEspecialidadId()))
                .descripcion(request.getDescripcion())
                .horarioApertura(request.getHorarioApertura())
                .horarioCierre(request.getHorarioCierre())
                .build();

        return repository.save(laboratorio);
    }

    private String generarCodigo(Long especialidadId) {
        Especialidad especialidad = especialidadService.buscarPorId(especialidadId);

        String abreviatura = especialidad.getAbreviatura();

        long cantidad = repository.countByCodigoStartingWith("LAB-" + abreviatura + "-");

        return String.format("LAB-%s-%02d", abreviatura, cantidad + 1);
    }
}