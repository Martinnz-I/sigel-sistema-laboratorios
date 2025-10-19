package com.sigel.SigelApi.repository;

import com.sigel.SigelApi.model.Refaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefaccionRepository extends JpaRepository<Refaccion, Long> {
}