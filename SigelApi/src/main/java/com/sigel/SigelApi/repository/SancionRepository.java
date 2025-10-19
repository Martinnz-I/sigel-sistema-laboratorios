package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.Sancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {
}