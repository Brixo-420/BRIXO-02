package com.brixo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "CERTIFICACION")
@Getter @Setter @NoArgsConstructor
public class Certificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_certificado")
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "entidad_emisora")
    private String entidadEmisora;

    @Column(name = "fecha_obtenida")
    private LocalDate fechaObtenida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contratista", nullable = false)
    private Contratista contratista;
}
