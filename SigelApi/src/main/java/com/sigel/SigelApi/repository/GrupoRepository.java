package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    List<Grupo> findBySemestre(Integer semestre);
    List<Grupo> findByEspecialidadId(Long especialidadId);
}