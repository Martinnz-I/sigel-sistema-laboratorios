package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.EstadisticaDiaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadisticaDiariaRepository extends JpaRepository<EstadisticaDiaria, Long> {
}