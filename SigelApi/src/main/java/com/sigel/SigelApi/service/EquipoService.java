package com.sigel.SigelApi.service;

import com.sigel.SigelApi.dto.EquipoAgrupadoDTO;
import com.sigel.SigelApi.dto.EquipoDTO;
import com.sigel.SigelApi.dto.NewEquipoRequest;
import com.sigel.SigelApi.enums.UserRole;
import com.sigel.SigelApi.exceptions.BadRequestException;
import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import com.sigel.SigelApi.repository.EquipoRepository;
import com.sigel.SigelApi.security.JwtUtil;
import com.sigel.SigelApi.service.implementation.EquipoImpl;
import com.sigel.SigelApi.model.Grupo;
import com.sigel.SigelApi.model.Laboratorio;
import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.model.Equipo;
import com.sigel.SigelApi.model.Ubicacion;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipoService implements EquipoImpl {
    private final EquipoRepository repository;
    private final QrService qrService;
    private final StorageService storageService;
    private final LaboratorioService laboratorioService;
    private final UbicacionService ubicacionService;
    private final ValidacionPrestamoService validacionPrestamoService;
    private final CategoriaEquipoService categoriaEquipoService;
    private final JwtUtil jwtUtil;
    private final MarcaService marcaService;

    @Override
    public List<EquipoAgrupadoDTO> obtenerCatalogoEquipos() {
        return repository.findEquiposAgrupadosDisponibles();
    }

    @Override
    public List<Equipo> listarEquiposPorLaboratorioId(Long laboratorioId) {
        return repository.findByLaboratorioId(laboratorioId);
    }

    @Override
    public Equipo buscarEquipoPorCodigo(String codigo) {
        return repository.findByCodigo(codigo).orElseThrow(() ->
                new ResourceNotFoundException("Equipo no encontrado")
        );
    }

    @Override
    public Equipo buscarEquipoPorId(Long idEquipo) {
        return repository.findById(idEquipo).orElseThrow(() ->
                new ResourceNotFoundException("Equipo no encontrado"));
    }

    @Override
    public EquipoDTO buscarEquipoPorIdDTO(Long idEquipo) {
        Equipo equipo = repository.findById(idEquipo).orElseThrow(() ->
                new ResourceNotFoundException("Equipo no encontrado"));

        return EquipoDTO.fromEntity(equipo);
    }

    @Override
    @Transactional
    public Equipo registrarEquipo(NewEquipoRequest request, List<MultipartFile> imagenes) {
        try {
            storageService.validarImagenes(imagenes, 5);

            Map<String, String> imagenesUrls = storageService.subirImagenes(imagenes);

            Laboratorio laboratorio = laboratorioService.buscarPorId(request.getLaboratorioId());
            Ubicacion ubicacion = ubicacionService.buscarUbicacionPorId(request.getUbicacionId());

            if(!laboratorio.getId().equals(ubicacion.getLaboratorio().getId())) {
                throw new BadRequestException("La ubicación no pertenece al laboratorio elegido");
            }

            String codigo = generarCodigoEquipo(laboratorio.getEspecialidad().getAbreviatura());

            System.out.println("Codigo del equipo: " + codigo);

            Equipo equipo = construirEquipo(request,ubicacion,laboratorio,codigo,imagenesUrls);

            return repository.save(equipo);
        } catch(DataIntegrityViolationException ex) {
            throw new BadRequestException("El equipo no pudo ser registrado porque ya existe uno con datos similares.");
        }
    }

    @Override
    public Long validarEquipoASolicitar(String codigoEquipo) {
        Equipo equipo = buscarEquipoPorCodigo(codigoEquipo);

        validacionPrestamoService.validarEquipoYObtenerSesion(equipo);

        return equipo.getId();
    }

    @Override
    public Equipo guardarEquipo(Equipo equipo) {
        return repository.save(equipo);
    }

    private String generarCodigoEquipo(String labCode) {
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return String.format("EQP-%s-%s",labCode,random);
    }

    private Equipo construirEquipo(NewEquipoRequest request, Ubicacion ubicacion, Laboratorio laboratorio,
                                   String codigo, Map<String, String> imagenesUrls) {
        return Equipo.builder()
                .codigo(codigo)
                .nombre(request.getNombre())
                .categoria(categoriaEquipoService.buscarCategoriaPorId(request.getCategoriaId()))
                .marca(marcaService.buscarMarcaPorId(request.getMarcaId()))
                .modelo(request.getModelo())
                .numeroSerie(request.getNumeroSerie())
                .laboratorio(laboratorio)
                .ubicacion(ubicacion)
                .especificaciones(request.getEspecificaciones())
                .disponiblePrestamo(request.getDisponiblePrestamo())
                .tipoPrestamo(request.getTipoPrestamo())
                .fotos(imagenesUrls)
                .estadoFisico(request.getEstadoFisico())
                .notas(request.getNotas())
                .codigoQr(qrService.generarYSubirQR(codigo))
                .build();
    }

    private void validarUsuarioParaPrestamoInterno(Grupo grupo) {
        Usuario usuario = jwtUtil.obtenerUsuarioToken();

        if(usuario.getRol() != UserRole.ALUMNO) {
            throw new BadRequestException("No puedes solicitar un prestamo interno si no eres un Alumno");
        }

        if(grupo != null && !usuario.getGrupo().getId().equals(grupo.getId())) {
            throw new BadRequestException("No puedes solicitar este equipo, no perteneces al grupo activo");
        }
    }
}