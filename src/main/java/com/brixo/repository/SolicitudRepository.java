package com.brixo.repository;

import com.brixo.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    List<Solicitud> findByClienteIdOrderByCreadoEnDesc(Integer clienteId);
    List<Solicitud> findByContratistaIdOrderByCreadoEnDesc(Integer contratistaId);
    List<Solicitud> findByEstadoOrderByCreadoEnDesc(Solicitud.Estado estado);
}
