package com.brixo.service;

import com.brixo.model.Cliente;
import com.brixo.model.Solicitud;
import com.brixo.repository.ClienteRepository;
import com.brixo.repository.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepo;
    private final ClienteRepository clienteRepo;

    public SolicitudService(SolicitudRepository solicitudRepo, ClienteRepository clienteRepo) {
        this.solicitudRepo = solicitudRepo;
        this.clienteRepo   = clienteRepo;
    }

    @Transactional
    public Solicitud crear(Integer idCliente, String titulo, String descripcion,
                           BigDecimal presupuesto, String ubicacion) {
        Cliente cliente = clienteRepo.findById(idCliente)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + idCliente));

        Solicitud s = new Solicitud();
        s.setCliente(cliente);
        s.setTitulo(titulo);
        s.setDescripcion(descripcion);
        s.setPresupuesto(presupuesto != null ? presupuesto : BigDecimal.ZERO);
        s.setUbicacion(ubicacion);
        s.setEstado(Solicitud.Estado.ABIERTA);
        return solicitudRepo.save(s);
    }

    public List<Solicitud> listarPorCliente(Integer idCliente) {
        return solicitudRepo.findByClienteIdOrderByCreadoEnDesc(idCliente);
    }

    public List<Solicitud> listarAbiertas() {
        return solicitudRepo.findByEstadoOrderByCreadoEnDesc(Solicitud.Estado.ABIERTA);
    }

    public Optional<Solicitud> findById(Integer id) {
        return solicitudRepo.findById(id);
    }

    @Transactional
    public void eliminar(Integer id) {
        solicitudRepo.deleteById(id);
    }
}
