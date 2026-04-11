package com.brixo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "COTIZACION_CONFIRMADA")
@Getter @Setter @NoArgsConstructor
public class CotizacionConfirmada {

    public enum Estado { pendiente, en_proceso, completada, cancelada }
    public enum Complejidad { bajo, medio, alto }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "servicio_principal", nullable = false, length = 255)
    private String servicioPrincipal;

    @Column(name = "materiales_json", nullable = false, columnDefinition = "JSON")
    private String materialesJson;

    @Column(name = "personal_json", nullable = false, columnDefinition = "JSON")
    private String personalJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Complejidad complejidad = Complejidad.medio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.pendiente;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @Column(name = "confirmado_en", nullable = false)
    private LocalDateTime confirmadoEn;
}
