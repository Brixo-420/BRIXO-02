package com.brixo.service;

import com.brixo.model.EstadoServicio;
import com.brixo.model.Servicio;
import com.brixo.model.Usuario;
import com.brixo.repository.ServicioRepository;
import com.brixo.repository.UsuarioRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final UsuarioRepository usuarioRepository;

    public ServicioService(ServicioRepository servicioRepository, UsuarioRepository usuarioRepository) {
        this.servicioRepository = servicioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Servicio> listarFiltrados(String titulo, String estado, String emailCliente, String usuarioActualEmail, boolean esAdmin, boolean esCliente) {
        Specification<Servicio> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (titulo != null && !titulo.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%"));
            }

            if (estado != null && !estado.isBlank()) {
                try {
                    predicates.add(cb.equal(root.get("estado"), EstadoServicio.valueOf(estado)));
                } catch (Exception e) {
                    // Ignorar filtro si el estado no es válido
                }
            }

            if (emailCliente != null && !emailCliente.isBlank() && esAdmin) {
                predicates.add(cb.like(cb.lower(root.get("cliente").get("email")), "%" + emailCliente.toLowerCase() + "%"));
            }

            if (esCliente && !esAdmin) {
                predicates.add(cb.equal(root.get("cliente").get("email"), usuarioActualEmail));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }; // Cierre correcto del Specification

        return servicioRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
    }

    public Servicio buscarPorId(Integer id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
    }

    @Transactional
    public void crear(Servicio servicio, String emailUsuarioActual) {
        Usuario cliente = usuarioRepository.findByEmail(emailUsuarioActual)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        servicio.setId(null);
        servicio.setCliente(cliente);
        servicioRepository.save(servicio);
    }

    @Transactional
    public void actualizar(Integer id, Servicio cambios, String emailUsuarioActual, boolean esAdmin) {
        Servicio actual = buscarPorId(id);
        validarPermiso(actual, emailUsuarioActual, esAdmin);

        actual.setTitulo(cambios.getTitulo());
        actual.setDescripcion(cambios.getDescripcion());
        actual.setUbicacion(cambios.getUbicacion());
        actual.setPresupuesto(cambios.getPresupuesto());
        actual.setEstado(cambios.getEstado());

        servicioRepository.save(actual);
    }

    @Transactional
    public void eliminar(Integer id, String emailUsuarioActual, boolean esAdmin) {
        Servicio actual = buscarPorId(id);
        validarPermiso(actual, emailUsuarioActual, esAdmin);
        servicioRepository.delete(actual);
    }

    private void validarPermiso(Servicio servicio, String email, boolean esAdmin) {
        if (esAdmin) return;
        if (servicio.getCliente() == null || !servicio.getCliente().getEmail().equals(email)) {
            throw new IllegalArgumentException("No tienes permiso para esta acción");
        }
    }
}