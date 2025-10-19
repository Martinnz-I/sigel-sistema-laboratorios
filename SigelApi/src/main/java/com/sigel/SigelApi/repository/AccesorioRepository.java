package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.Accesorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccesorioRepository extends JpaRepository<Accesorio, Long> {
}