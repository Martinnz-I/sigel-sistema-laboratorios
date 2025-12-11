package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
    Optional<Ubicacion> findByIdAndLaboratorioId(Long ubicacionId, Long laboratorioId);
}