package com.sigel.SigelApi.service;

import com.sigel.SigelApi.enums.EquipmentStatus;
import com.sigel.SigelApi.enums.EstadoSesionLab;
import com.sigel.SigelApi.enums.UserRole;
import com.sigel.SigelApi.exceptions.BadRequestException;
import com.sigel.SigelApi.model.Equipo;
import com.sigel.SigelApi.model.Grupo;
import com.sigel.SigelApi.model.SesionLaboratorio;
import com.sigel.SigelApi.model.Usuario;
import com.sigel.SigelApi.repository.SesionLaboratorioRepository;
import com.sigel.SigelApi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidacionPrestamoService {
    private final SesionLaboratorioRepository sesionLaboratorioRepository;
    private final PrestamoDetalleService prestamoDetalleService;
    private final JwtUtil jwtUtil;

    public Usuario validarUsuarioParaPrestamoInterno(Grupo grupo) {
        Usuario usuario = jwtUtil.obtenerUsuarioToken();

        if(usuario.getRol() != UserRole.ALUMNO) {
            throw new BadRequestException("No puedes solicitar un préstamo interno si no eres un Alumno");
        }

        if(grupo != null && !usuario.getGrupo().getId().equals(grupo.getId())) {
            throw new BadRequestException("No puedes solicitar este equipo, no perteneces al grupo activo");
        }

        return usuario;
    }

    public void validarDisponibilidadEquipo(Equipo equipo) {
        if(prestamoDetalleService.validarEquipoOcupado(equipo.getId())) {
            throw new BadRequestException("El equipo ya fue tomado por otro alumno");
        }
        if(equipo.getEstado() != EquipmentStatus.DISPONIBLE) {
            throw new BadRequestException("El equipo no está disponible");
        }
    }

    public SesionLaboratorio obtenerSesionActivaDelEquipo(Equipo equipo) {
        return sesionLaboratorioRepository
                .findByLaboratorioIdAndEstado(
                        equipo.getLaboratorio().getId(),
                        EstadoSesionLab.ACTIVA
                )
                .orElseThrow(() -> new BadRequestException(
                        "La sesión del laboratorio ya no está activa"
                ));
    }

    public SesionLaboratorio validarEquipoYObtenerSesion(Equipo equipo) {
        SesionLaboratorio sesion = obtenerSesionActivaDelEquipo(equipo);

        if (prestamoDetalleService.validarEquipoOcupado(equipo.getId())) {
            throw new BadRequestException(
                    "El equipo '" + equipo.getNombre() + "' ya se encuentra en préstamo activo"
            );
        }

        validarUsuarioParaPrestamoInterno(sesion.getGrupo());

        return sesion;
    }
}