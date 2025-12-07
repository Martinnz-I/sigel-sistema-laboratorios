package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.dto.EquipoAgrupadoDTO;
import com.sigel.SigelApi.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    @Query("""
    SELECT new com.sigel.SigelApi.dto.EquipoAgrupadoDTO(
        e.nombre,
        e.categoria.id,
        e.categoria.nombre,
        e.marca.id,
        e.marca.nombre,
        e.modelo,
        e.laboratorio.especialidad.id,
        e.laboratorio.especialidad.nombre,
        COUNT(e.id)
    )
    FROM Equipo e
    WHERE e.activo = true
    AND e.disponiblePrestamo = true
    AND e.estado = 'DISPONIBLE'
    AND (e.tipoPrestamo = 'EXTERNO' OR e.tipoPrestamo = 'AMBOS')
    GROUP BY e.nombre, e.categoria.id, e.categoria.nombre,
             e.marca.id, e.marca.nombre, e.modelo,
             e.laboratorio.especialidad.id, e.laboratorio.especialidad.nombre
    ORDER BY e.nombre ASC
    """)
    List<EquipoAgrupadoDTO> findEquiposAgrupadosDisponibles();

    Optional<Equipo> findByCodigo(String codigo);

    List<Equipo> findByLaboratorioId(Long laboratorioId);
}