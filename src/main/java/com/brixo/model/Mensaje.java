package com.brixo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "MENSAJE")
@Getter @Setter @NoArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Integer id;

    @Column(name = "remitente_id", nullable = false)
    private Integer remitenteId;

    @Column(name = "remitente_rol", nullable = false, length = 20)
    private String remitenteRol;

    @Column(name = "destinatario_id", nullable = false)
    private Integer destinatarioId;

    @Column(name = "destinatario_rol", nullable = false, length = 20)
    private String destinatarioRol;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false)
    private boolean leido = false;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @PrePersist
    void prePersist() {
        if (creadoEn == null) creadoEn = LocalDateTime.now();
    }
}
