package com.brixo.repository;

import com.brixo.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {

    @Query("""
        SELECT m FROM Mensaje m
        WHERE (m.remitenteId = :userId AND m.remitenteRol = :rol
                AND m.destinatarioId = :otherId AND m.destinatarioRol = :otherRol)
           OR (m.remitenteId = :otherId AND m.remitenteRol = :otherRol
                AND m.destinatarioId = :userId AND m.destinatarioRol = :rol)
        ORDER BY m.creadoEn ASC
        """)
    List<Mensaje> findConversacion(
        @Param("userId") Integer userId, @Param("rol") String rol,
        @Param("otherId") Integer otherId, @Param("otherRol") String otherRol
    );

    @Modifying
    @Query("""
        UPDATE Mensaje m SET m.leido = true
        WHERE m.destinatarioId = :userId AND m.destinatarioRol = :rol
          AND m.remitenteId = :otherId AND m.remitenteRol = :otherRol
          AND m.leido = false
        """)
    void marcarComoLeidos(
        @Param("userId") Integer userId, @Param("rol") String rol,
        @Param("otherId") Integer otherId, @Param("otherRol") String otherRol
    );

    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.destinatarioId = :userId AND m.destinatarioRol = :rol AND m.leido = false")
    long countNoLeidos(@Param("userId") Integer userId, @Param("rol") String rol);
}
