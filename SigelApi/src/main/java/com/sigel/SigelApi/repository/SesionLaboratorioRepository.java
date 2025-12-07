package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.enums.EstadoSesionLab;
import com.sigel.SigelApi.model.SesionLaboratorio;
import com.sigel.SigelApi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SesionLaboratorioRepository extends JpaRepository<SesionLaboratorio, Long> {
    int countByLaboratorioCodigoAndFechaInicioBetween(
            String codigoLaboratorio,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    Optional<SesionLaboratorio> findByLaboratorioIdAndEstado(
            Long laboratorioId,
            EstadoSesionLab estado
    );

    @Query("""
        SELECT s
        FROM SesionLaboratorio s
        WHERE s.estado = 'ACTIVA'
        AND s.fechaFinEstimada < :limiteGracia
        ORDER BY s.fechaFinEstimada ASC
        """)
    List<SesionLaboratorio> findSesionesActivasVencidas(
            @Param("limiteGracia") LocalDateTime limiteGracia
    );

    boolean existsByMaestroIdAndEstado(
            Long maestroId,
            EstadoSesionLab estado
    );

    Optional<SesionLaboratorio> findByGrupoIdAndEstado(
            Long grupoId,
            EstadoSesionLab estado
    );
}