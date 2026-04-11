package com.brixo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONTRATISTA")
@Getter @Setter @NoArgsConstructor
public class Contratista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contratista")
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    private String telefono;
    private String ciudad;

    @Column(name = "ubicacion_mapa")
    private String ubicacionMapa;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column(columnDefinition = "TEXT")
    private String experiencia;

    @Column(columnDefinition = "TEXT")
    private String portafolio;

    @Column(name = "descripcion_perfil", columnDefinition = "TEXT")
    private String descripcionPerfil;

    @Column(nullable = false)
    private boolean verificado = false;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @PrePersist
    void prePersist() {
        if (creadoEn == null) creadoEn = LocalDateTime.now();
    }
}
