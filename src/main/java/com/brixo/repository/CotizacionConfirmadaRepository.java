package com.brixo.repository;

import com.brixo.model.CotizacionConfirmada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CotizacionConfirmadaRepository extends JpaRepository<CotizacionConfirmada, Integer> {
    List<CotizacionConfirmada> findByIdClienteOrderByConfirmadoEnDesc(Integer idCliente);
}
