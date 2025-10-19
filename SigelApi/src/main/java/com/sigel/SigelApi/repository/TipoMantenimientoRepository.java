package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.TipoMantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoMantenimientoRepository extends JpaRepository<TipoMantenimiento, Long> {
}