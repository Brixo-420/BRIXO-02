package com.brixo.security;

import com.brixo.repository.ClienteRepository;
import com.brixo.repository.ContratistaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Busca usuarios en CLIENTE o CONTRATISTA por correo electrónico.
 * El correo actúa como "username" de Spring Security.
 */
@Service
public class BrixoUserDetailsService implements UserDetailsService {

    private final ClienteRepository clienteRepo;
    private final ContratistaRepository contratistaRepo;

    public BrixoUserDetailsService(ClienteRepository clienteRepo,
                                   ContratistaRepository contratistaRepo) {
        this.clienteRepo      = clienteRepo;
        this.contratistaRepo  = contratistaRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // 1. Buscar en CLIENTE
        var cliente = clienteRepo.findByCorreo(correo);
        if (cliente.isPresent()) {
            return new BrixoUserDetails(cliente.get());
        }

        // 2. Buscar en CONTRATISTA
        var contratista = contratistaRepo.findByCorreo(correo);
        if (contratista.isPresent()) {
            return new BrixoUserDetails(contratista.get());
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
    }
}
