package com.brixo.repository;

import com.brixo.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    List<Servicio> findByCategoriaId(Integer categoriaId);
}
