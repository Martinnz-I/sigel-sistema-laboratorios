package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.dto.PrestamoInternoActivoDTO;
import com.sigel.SigelApi.enums.LoanStatus;
import com.sigel.SigelApi.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findBySesionLaboratorioIdAndEstado(
            Long sesionLaboratorioId,
            LoanStatus estado
    );

    Prestamo findBySesionLaboratorioIdAndUsuarioIdAndEstado(
            Long sesionLaboratorioId,
            Long usuarioId,
            LoanStatus estado
    );

    @Query("SELECT new com.sigel.SigelApi.dto.PrestamoInternoActivoDTO(" +
            "p.id, " +
            "sl.id, " +
            "l.nombre, " +
            "p.materia, " +
            "p.fechaSolicitud, " +
            "p.fechaDevolucionEstimada, " +
            "p.estado) " +
            "FROM Prestamo p " +
            "JOIN p.sesionLaboratorio sl " +
            "JOIN sl.laboratorio l " +
            "WHERE sl.id = :sesionLaboratorioId " +
            "AND p.usuario.id = :usuarioId " +
            "AND p.estado = 'ACTIVO'")
    Optional<PrestamoInternoActivoDTO> findPrestamoActivoDTOBySesionAndUsuario(
            @Param("sesionLaboratorioId") Long sesionLaboratorioId,
            @Param("usuarioId") Long usuarioId
    );


}