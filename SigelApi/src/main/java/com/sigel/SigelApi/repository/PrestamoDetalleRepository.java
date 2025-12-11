package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.enums.LoanStatus;
import com.sigel.SigelApi.model.PrestamoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrestamoDetalleRepository extends JpaRepository<PrestamoDetalle, Long> {
    Optional<PrestamoDetalle> findFirstByEquipoIdOrderByPrestamoCreatedAtDesc(Long equipoId);

    List<PrestamoDetalle> findByPrestamoId(Long prestamoId);

    boolean existsByEquipoIdAndPrestamoEstado(
            Long equipoId,
            LoanStatus estado
    );
}