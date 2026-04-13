package com.brixo.config;

import com.brixo.model.*;
import com.brixo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoriaRepository categoriaRepository;
    private final ServicioRepository servicioRepository;
    private final UbicacionRepository ubicacionRepository;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository, 
                           PasswordEncoder passwordEncoder, CategoriaRepository categoriaRepository,
                           ServicioRepository servicioRepository, UbicacionRepository ubicacionRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoriaRepository = categoriaRepository;
        this.servicioRepository = servicioRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    @Override
    public void run(String... args) {
        // ===== INICIALIZAR ROLES =====
        crearRolSiNoExiste("CLIENTE");
        crearRolSiNoExiste("CONTRATISTA");
        Rol rolAdmin = crearRolSiNoExiste("ADMIN");

        // ===== ENCRIPTAR CONTRASEÑAS EXISTENTES =====
        usuarioRepository.findAll().forEach(usuario -> {
            String password = usuario.getPassword();
            if (password != null && !password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                usuario.setPassword(passwordEncoder.encode(password));
                usuarioRepository.save(usuario);
            }
        });

        // ===== CREAR USUARIO ADMIN =====
        if (usuarioRepository.findByEmail("admin@brixo.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador Brixo");
            admin.setEmail("admin@brixo.com");
            admin.setPassword(passwordEncoder.encode("Admin123*"));
            admin.setRol(rolAdmin);
            usuarioRepository.save(admin);
        }

        // ===== INICIALIZAR CATEGORÍAS =====
        if (categoriaRepository.count() == 0) {
            Categoria reparacion = new Categoria();
            reparacion.setNombre("Reparación");
            reparacion.setDescripcion("Servicios de reparación y mantenimiento");
            categoriaRepository.save(reparacion);

            Categoria limpieza = new Categoria();
            limpieza.setNombre("Limpieza");
            limpieza.setDescripcion("Servicios de limpieza general y especializada");
            categoriaRepository.save(limpieza);

            Categoria construccion = new Categoria();
            construccion.setNombre("Construcción");
            construccion.setDescripcion("Servicios de construcción y remodelación");
            categoriaRepository.save(construccion);

            Categoria plomeria = new Categoria();
            plomeria.setNombre("Plomería");
            plomeria.setDescripcion("Servicios de plomería e instalación de tuberías");
            categoriaRepository.save(plomeria);
        }

        // ===== INICIALIZAR SERVICIOS =====
        if (servicioRepository.count() == 0) {
            Categoria reparacion = categoriaRepository.findAll().stream()
                    .filter(c -> "Reparación".equals(c.getNombre()))
                    .findFirst()
                    .orElse(null);

            if (reparacion != null) {
                Servicio reparacionElectrica = new Servicio();
                reparacionElectrica.setNombre("Reparación Eléctrica");
                reparacionElectrica.setDescripcion("Reparación y mantenimiento de sistemas eléctricos");
                reparacionElectrica.setPrecioEstimado(new BigDecimal("100.00"));
                reparacionElectrica.setCategoria(reparacion);
                servicioRepository.save(reparacionElectrica);

                Servicio reparacionDaños = new Servicio();
                reparacionDaños.setNombre("Reparación de Daños");
                reparacionDaños.setDescripcion("Reparación de daños estructurales menores");
                reparacionDaños.setPrecioEstimado(new BigDecimal("150.00"));
                reparacionDaños.setCategoria(reparacion);
                servicioRepository.save(reparacionDaños);
            }
        }

        // ===== INICIALIZAR UBICACIONES =====
        if (ubicacionRepository.count() == 0) {
            Ubicacion bogota = new Ubicacion();
            bogota.setCiudad("Bogotá");
            bogota.setDepartamento("Cundinamarca");
            bogota.setDireccion("Calle 50 #10-20");
            bogota.setLatitud(new BigDecimal("4.7110"));
            bogota.setLongitud(new BigDecimal("-74.0055"));
            ubicacionRepository.save(bogota);

            Ubicacion medellin = new Ubicacion();
            medellin.setCiudad("Medellín");
            medellin.setDepartamento("Antioquia");
            medellin.setDireccion("Carrera 45 #50-100");
            medellin.setLatitud(new BigDecimal("6.2442"));
            medellin.setLongitud(new BigDecimal("-75.5812"));
            ubicacionRepository.save(medellin);

            Ubicacion cali = new Ubicacion();
            cali.setCiudad("Cali");
            cali.setDepartamento("Valle del Cauca");
            cali.setDireccion("Avenida 6 #20-50");
            cali.setLatitud(new BigDecimal("3.4372"));
            cali.setLongitud(new BigDecimal("-76.5225"));
            ubicacionRepository.save(cali);
        }
    }

    // Método helper: crea un Rol solo si no existe
    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> rolRepository.save(new Rol(nombre)));
    }
}
