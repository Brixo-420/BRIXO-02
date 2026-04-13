package com.brixo.repository;

import com.brixo.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Integer>, JpaSpecificationExecutor<Servicio> {
    List<Servicio> findByCategoriaId(Integer categoriaId);
}
