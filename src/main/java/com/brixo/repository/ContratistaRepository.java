package com.brixo.repository;

import com.brixo.model.Contratista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContratistaRepository extends JpaRepository<Contratista, Integer> {
    Optional<Contratista> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    List<Contratista> findByCiudadContainingIgnoreCase(String ciudad);
}
