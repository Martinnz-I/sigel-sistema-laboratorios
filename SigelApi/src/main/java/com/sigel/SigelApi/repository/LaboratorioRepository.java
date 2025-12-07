package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {
    long countByCodigoStartingWith(String codigo);

    List<Laboratorio> findByActivoTrue();
}